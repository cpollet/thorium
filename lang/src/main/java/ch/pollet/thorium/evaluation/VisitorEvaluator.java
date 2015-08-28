/*
 * Copyright 2015 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.pollet.thorium.evaluation;

import ch.pollet.thorium.antlr.ThoriumBaseVisitor;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException;
import ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException;
import ch.pollet.thorium.semantic.exception.InvalidTypeException;
import ch.pollet.thorium.semantic.exception.MethodNotFoundException;
import ch.pollet.thorium.semantic.exception.SymbolNotFoundException;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.values.Constant;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;
import ch.pollet.thorium.values.Variable;

/**
 * @author Christophe Pollet
 */
public class VisitorEvaluator extends ThoriumBaseVisitor<Void> {
    private EvaluationContext context;

    public VisitorEvaluator(EvaluationContext context) {
        this.context = context;
    }

    //region Statements
    @Override
    public Void visitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        super.visitUnconditionalStatement(ctx);
        context.lastStatementValue = context.popStack();

        return null;
    }

    @Override
    public Void visitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        visitStatementsInNestedContext(ctx.statements());

        return null;
    }

    private void visitStatementsInNestedContext(ThoriumParser.StatementsContext ctx) {
        context = context.createChild();

        visitStatements(ctx);

        context = context.destroyAndRestoreParent();
    }

    @Override
    public Void visitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        // FIXME SEM add check that potential assigned variable is already defined in symbol table
        if (isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.lastStatementValue = context.popStack();
        } else {
            context.lastStatementValue = DirectValue.build();
        }

        return null;
    }

    private boolean isExpressionTrue(ThoriumParser.ExpressionContext expression) {
        visit(expression);

        Value condition = context.popStack();

        // TODO SEM: move this
        if (condition.type() != Type.BOOLEAN) {
            throw new InvalidTypeException("Boolean expected, got " + condition.type());
        }

        return condition.value().equals(DirectValue.build(true));
    }

    @Override
    public Void visitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        // FIXME SEM add check that potential assigned variable is already defined in symbol table
        if (!isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.lastStatementValue = context.popStack();
        } else {
            context.lastStatementValue = DirectValue.build();
        }

        return null;
    }
    //endregion

    //region Expressions
    @Override
    public Void visitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        super.visitMultiplicationExpression(ctx);
        evalOperator("*");

        return null;
    }

    @Override
    public Void visitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        super.visitAdditionExpression(ctx);
        evalOperator("+");

        return null;
    }

    private void evalOperator(String operator) {
        Value right = context.popStack();
        Value left = context.popStack();

        Operator op = left.type().lookupMethod(new MethodMatcher(operator, right.type()));

        // TODO SEM move this
        if (op == null) {
            throw new MethodNotFoundException("Method " + operator + "(" + right.type() + ") not implemented on " + left.type());
        }

        context.pushStack(op.apply(left, right));
    }

    @Override
    public Void visitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        super.visitAssignmentExpression(ctx);

        Value right = context.popStack();
        Value left = context.popStack();

        assertValidAssignment(left, right);

        // TODO refactor without instanceof?
        Symbol symbol;
        if (left instanceof Constant) {
            symbol = new Constant(left.getName(), right.value());
        } else if (left instanceof Variable) {
            symbol = new Variable(left.getName(), right.value());
        } else {
            throw new IllegalStateException();
        }

        context.insertSymbol(symbol);
        context.pushStack(symbol.value());

        return null;
    }

    private void assertValidAssignment(Value left, Value right) {
        // TODO SEM: move this
        if (!left.isWritable()) {
            throw new InvalidAssignmentTargetException("Cannot assign " + right.toString() + " to " + left.toString());
        }

        if (right.type() == Type.VOID) {
            throw new InvalidAssignmentSourceException("Cannot assign from " + right.toString());
        }


        if (!Type.isAssignableFrom(left.type(), right.type())) {
            throw new InvalidTypeException(right.type() + " is no assignable to " + left.type());
        }
    }

    @Override
    public Void visitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        visitBlock(ctx.block());
        context.pushStack(context.lastStatementValue);

        return null;
    }
    //endregion

    //region If Statement
    @Override
    public Void visitIfStatement(ThoriumParser.IfStatementContext ctx) {
        context = context.createChild();

        visitNestedIfStatement(ctx);

        context = context.destroyAndRestoreParent();

        return null;
    }

    private void visitNestedIfStatement(ThoriumParser.IfStatementContext ctx) {
        if (isExpressionTrue(ctx.expression())) {
            visitStatements(ctx.statements());
        } else if (ctx.elseStatement() != null) {
            visitElseStatement(ctx.elseStatement());
        } else {
            context.lastStatementValue = DirectValue.build();
        }
    }

    @Override
    public Void visitElseStatement(ThoriumParser.ElseStatementContext ctx) {
        if (ctx.statements() != null) {
            visitStatements(ctx.statements());
        } else if (ctx.ifStatement() != null) {
            visitNestedIfStatement(ctx.ifStatement());
        } else {
            throw new IllegalStateException();
        }

        return null;
    }
    //endregion

    //region Values
    @Override
    public Void visitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        context.pushStack(DirectValue.build(Long.valueOf(ctx.IntegerLiteral().getText())));

        return null;
    }

    @Override
    public Void visitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        context.pushStack(DirectValue.build(Double.valueOf(ctx.FloatLiteral().getText())));

        return null;
    }

    @Override
    public Void visitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        context.pushStack(DirectValue.build(Boolean.valueOf(ctx.BooleanLiteral().getText())));

        return null;
    }

    @Override
    public Void visitVariableName(ThoriumParser.VariableNameContext ctx) {
        Symbol symbol;

        try {
            symbol = context.lookupSymbol(ctx.getText());
        } catch (SymbolNotFoundException e) {
            symbol = new Variable(ctx.getText()); // TODO EVAL: should be symbol reference instead?
        }

        context.pushStack(symbol);

        return null;
    }

    @Override
    public Void visitConstantName(ThoriumParser.ConstantNameContext ctx) {
        Symbol symbol;

        try {
            symbol = context.lookupSymbol(ctx.getText());
        } catch (SymbolNotFoundException e) {
            symbol = new Constant(ctx.getText()); // TODO EVAL: should be symbol reference instead?
        }

        context.pushStack(symbol);

        return null;
    }
    //endregion
}
