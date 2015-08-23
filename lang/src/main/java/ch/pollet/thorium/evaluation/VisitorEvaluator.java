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
import ch.pollet.thorium.values.Constant;
import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;
import ch.pollet.thorium.values.Variable;
import ch.pollet.thorium.values.types.BooleanType;
import ch.pollet.thorium.values.types.FloatType;
import ch.pollet.thorium.values.types.IntegerType;
import ch.pollet.thorium.values.types.NullType;
import ch.pollet.thorium.values.types.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class VisitorEvaluator extends ThoriumBaseVisitor<Void> {
    private EvaluationContext context;

    private Map<OperationSignature, Operator<Type, Type, Type>> operators = new HashMap<OperationSignature, Operator<Type, Type, Type>>() {{
        put(new OperationSignature("+", IntegerType.class, IntegerType.class), (left, right) -> ((IntegerType) left).operatorPlus((IntegerType) right));
        put(new OperationSignature("+", FloatType.class, FloatType.class), (left, right) -> ((FloatType) left).operatorPlus((FloatType) right));
        put(new OperationSignature("+", IntegerType.class, FloatType.class), (left, right) -> ((IntegerType) left).operatorPlus((FloatType) right));
        put(new OperationSignature("+", FloatType.class, IntegerType.class), (left, right) -> ((FloatType) left).operatorPlus((IntegerType) right));
        put(new OperationSignature("+", BooleanType.class, BooleanType.class), (left, right) -> ((BooleanType) left).operatorPlus((BooleanType) right));

        put(new OperationSignature("*", IntegerType.class, IntegerType.class), (left, right) -> ((IntegerType) left).operatorMultiply((IntegerType) right));
        put(new OperationSignature("*", FloatType.class, FloatType.class), (left, right) -> ((FloatType) left).operatorMultiply((FloatType) right));
        put(new OperationSignature("*", IntegerType.class, FloatType.class), (left, right) -> ((IntegerType) left).operatorMultiply((FloatType) right));
        put(new OperationSignature("*", FloatType.class, IntegerType.class), (left, right) -> ((FloatType) left).operatorMultiply((IntegerType) right));
        put(new OperationSignature("*", BooleanType.class, BooleanType.class), (left, right) -> ((BooleanType) left).operatorMultiply((BooleanType) right));
    }};

    public VisitorEvaluator(EvaluationContext context) {
        this.context = context;
    }

    @Override
    public Void visitStatement(ThoriumParser.StatementContext ctx) {
        super.visitStatement(ctx);
        context.lastStatementValue = context.popStack();

        return null;
    }

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

        Operator<Type, Type, Type> op = operators.get(new OperationSignature(operator, left.getType(), right.getType()));

        // TODO SEM move this
        if (op == null) {
            throw new MethodNotFoundException("Method " + operator + "(" + Value.typeName(right) + ") not implemented on " + Value.typeName(left));
        }

        context.pushStack(op.apply(left.getValue(), right.getValue()));
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
            symbol = new Constant(left.getName(), right);
        } else if (left instanceof Variable) {
            symbol = new Variable(left.getName(), right);
        } else {
            throw new IllegalStateException();
        }

        context.insertSymbol(symbol);
        context.pushStack(symbol.getValue());

        return null;
    }

    private void assertValidAssignment(Value left, Value right) {
        // TODO SEM: move this
        if (!left.isWritable()) {
            throw new InvalidAssignmentTargetException("Cannot assign " + right.toString() + " to " + left.toString());
        }

        if (right.getType() == null) {
            throw new InvalidAssignmentSourceException("Cannot assign from " + right.toString());
        }

        if (!Type.isAssignableFrom(left.getType(), right.getType())) {
            throw new InvalidTypeException(Value.typeName(right) + " is no assignable to " + Value.typeName(left));
        }
    }

    @Override
    public Void visitBlock(ThoriumParser.BlockContext ctx) {
        context = context.createChild();

        super.visitBlock(ctx);

        context = context.destroyAndRestoreParent();
        context.pushStack(context.lastStatementValue);

        return null;
    }

    @Override
    public Void visitIfBlock(ThoriumParser.IfBlockContext ctx) {
        visit(ctx.expression());

        Value condition = context.popStack();

        // TODO SEM: move this
        if (condition.getType() != BooleanType.class) {
            throw new InvalidTypeException("Boolean expected, got " + Value.typeName(condition));
        }

        if (condition.equals(BooleanType.TRUE)) {
            visitBlock(ctx.block());
        } else if (ctx.elseBlock() != null) {
            if (ctx.elseBlock().block() != null) {
                visitBlock(ctx.elseBlock().block());
            } else {
                visitIfBlock(ctx.elseBlock().ifBlock());
            }
        } else {
            context.pushStack(NullType.NULL);
            context.lastStatementValue = NullType.NULL;
        }

        return null;
    }

    @Override
    public Void visitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        context.pushStack(new IntegerType(Long.valueOf(ctx.IntegerLiteral().getText())));

        return null;
    }

    @Override
    public Void visitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        context.pushStack(new FloatType(Double.valueOf(ctx.FloatLiteral().getText())));

        return null;
    }

    @Override
    public Void visitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        context.pushStack(BooleanType.build(Boolean.valueOf(ctx.BooleanLiteral().getText())));

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
}
