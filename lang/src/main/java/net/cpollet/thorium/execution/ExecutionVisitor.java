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

package net.cpollet.thorium.execution;

import net.cpollet.thorium.antlr.ThoriumBaseVisitor;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.execution.values.Symbol;
import net.cpollet.thorium.execution.values.Variable;
import net.cpollet.thorium.values.DirectValue;
import net.cpollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class ExecutionVisitor extends ThoriumBaseVisitor<Void> {
    private ExecutionContext context;

    public ExecutionVisitor(ExecutionContext context) {
        this.context = context;
    }

    //region Statements

    @Override
    public Void visitVariableDeclarationStatement(ThoriumParser.VariableDeclarationStatementContext ctx) {
        createSymbol(ctx.LCFirstIdentifier().getText(), ctx.expression());

        return null;
    }

    @Override
    public Void visitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        createSymbol(ctx.UCIdentifier().getText(), ctx.expression());

        return null;
    }

    private Symbol createSymbol(String identifier, ThoriumParser.ExpressionContext exprCtx) {
        if (!context.symbolDefined(identifier)) {
            Symbol symbol = new Variable(identifier); // TODO EVAL: should be symbol reference instead?

            context.insertSymbol(symbol);

            if (exprCtx != null) {
                visit(exprCtx);
                symbol.setValue(context.popStack().value());
                symbol.setType(symbol.value().type());
            }

            return symbol;
        }

        return context.lookupSymbol(identifier);
    }

    @Override
    public Void visitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        super.visitUnconditionalStatement(ctx);
        context.setLastStatementValue(context.popStack());

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
        if (isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.setLastStatementValue(context.popStack());
        } else {
            context.setLastStatementValue(DirectValue.build());
        }

        return null;
    }

    private boolean isExpressionTrue(ThoriumParser.ExpressionContext expression) {
        visit(expression);

        Value condition = context.popStack();

        return condition.value().equals(DirectValue.build(true));
    }

    @Override
    public Void visitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        if (!isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.setLastStatementValue(context.popStack());
        } else {
            context.setLastStatementValue(DirectValue.build());
        }

        return null;
    }

    @Override
    public Void visitRepeatedWhileStatement(ThoriumParser.RepeatedWhileStatementContext ctx) {
        context.setLastStatementValue(DirectValue.build());

        while (isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.setLastStatementValue(context.popStack());
        }

        return null;
    }

    @Override
    public Void visitRepeatedUntilStatement(ThoriumParser.RepeatedUntilStatementContext ctx) {
        context.setLastStatementValue(DirectValue.build());

        while (!isExpressionTrue(ctx.expression(1))) {
            visit(ctx.expression(0));
            context.setLastStatementValue(context.popStack());
        }

        return null;
    }

    //endregion

    //region Expressions

    @Override
    public Void visitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        super.visitMultiplicationExpression(ctx);
        evalBinaryOperator(ctx.op.getText());

        return null;
    }

    private void evalBinaryOperator(String operator) {
        Value right = context.popStack();
        Value left = context.popStack();

        Method method = left.type().lookupMethod(operator, right.type());

        context.pushStack(method.getMethodBody().apply(left, right));
    }

    @Override
    public Void visitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        super.visitAdditionExpression(ctx);
        evalBinaryOperator(ctx.op.getText());

        return null;
    }

    @Override
    public Void visitOrderComparisonExpression(ThoriumParser.OrderComparisonExpressionContext ctx) {
        super.visitOrderComparisonExpression(ctx);
        evalBinaryOperator(ctx.op.getText());

        return null;
    }

    @Override
    public Void visitNotExpression(ThoriumParser.NotExpressionContext ctx) {
        super.visitNotExpression(ctx);

        evalUnaryOperator(ctx.op.getText());
        return null;
    }

    private void evalUnaryOperator(String operator) {
        Value value = context.popStack();

        Method method = value.type().lookupMethod(operator);

        context.pushStack(method.getMethodBody().apply(value));
    }

    @Override
    public Void visitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        super.visitAssignmentExpression(ctx);

        Value right = context.popStack();
        Value left = context.popStack();

        Symbol symbol = context.lookupSymbol(left.getName());

        symbol.setValue(right.value());
        symbol.setType(right.type());

        context.pushStack(right.value());

        return null;
    }

    @Override
    public Void visitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        visitBlock(ctx.block());
        context.pushStack(context.getLastStatementValue());

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
            context.setLastStatementValue(DirectValue.build());
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

    //region Loop Statements

    @Override
    public Void visitWhileLoopStatement(ThoriumParser.WhileLoopStatementContext ctx) {
        context = context.createChild();

        while (isExpressionTrue(ctx.expression())) {
            visit(ctx.statements());
        }

        context = context.destroyAndRestoreParent();

        return null;
    }

    @Override
    public Void visitForLoopStatement(ThoriumParser.ForLoopStatementContext ctx) {
        context = context.createChild();

        if (ctx.init != null) {
            visit(ctx.init);
            context.popStack(); // discard, we don't need it
        }

        while (ctx.condition == null || isExpressionTrue(ctx.condition)) {
            visit(ctx.statements());
            if (ctx.increment != null) {
                visit(ctx.increment);
                context.popStack(); // discard, we don't need it
            }
        }

        context = context.destroyAndRestoreParent();

        return null;
    }

    @Override
    public Void visitForLoopStatementInitExpression(ThoriumParser.ForLoopStatementInitExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Void visitForLoopStatementInitVariableDeclaration(ThoriumParser.ForLoopStatementInitVariableDeclarationContext ctx) {
        Symbol symbol = createSymbol(ctx.LCFirstIdentifier().getText(), ctx.expression());
        context.pushStack(symbol.value());

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
        Symbol symbol = createSymbol(ctx.LCFirstIdentifier().getText(), null);

        context.pushStack(symbol);

        return null;
    }

    @Override
    public Void visitConstantName(ThoriumParser.ConstantNameContext ctx) {
        Symbol symbol = createSymbol(ctx.UCIdentifier().getText(), null);

        context.pushStack(symbol);

        return null;
    }

    //endregion
}
