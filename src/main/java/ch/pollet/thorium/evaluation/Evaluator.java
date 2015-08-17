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

import ch.pollet.thorium.antlr.ThoriumBaseListener;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.semantic.exception.InvalidAssignmentSourceException;
import ch.pollet.thorium.semantic.exception.InvalidAssignmentTargetException;
import ch.pollet.thorium.semantic.exception.InvalidTypeException;
import ch.pollet.thorium.semantic.exception.SymbolNotFoundException;
import ch.pollet.thorium.values.Constant;
import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;
import ch.pollet.thorium.values.Variable;
import ch.pollet.thorium.values.types.FloatType;
import ch.pollet.thorium.values.types.IntegerType;
import ch.pollet.thorium.values.types.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class Evaluator extends ThoriumBaseListener {
    private EvaluationContext context;

    private Map<OperationSignature, Operator<Type, Type, Type>> operators = new HashMap<OperationSignature, Operator<Type, Type, Type>>() {{
        put(new OperationSignature("+", IntegerType.class, IntegerType.class), (left, right) -> ((IntegerType) left).operatorPlus((IntegerType) right));
        put(new OperationSignature("+", FloatType.class, FloatType.class), (left, right) -> ((FloatType) left).operatorPlus((FloatType) right));
        put(new OperationSignature("+", IntegerType.class, FloatType.class), (left, right) -> ((IntegerType) left).operatorPlus((FloatType) right));
        put(new OperationSignature("+", FloatType.class, IntegerType.class), (left, right) -> ((FloatType) left).operatorPlus((IntegerType) right));

        put(new OperationSignature("*", IntegerType.class, IntegerType.class), (left, right) -> ((IntegerType) left).operatorMultiply((IntegerType) right));
        put(new OperationSignature("*", FloatType.class, FloatType.class), (left, right) -> ((FloatType) left).operatorMultiply((FloatType) right));
        put(new OperationSignature("*", IntegerType.class, FloatType.class), (left, right) -> ((IntegerType) left).operatorMultiply((FloatType) right));
        put(new OperationSignature("*", FloatType.class, IntegerType.class), (left, right) -> ((FloatType) left).operatorMultiply((IntegerType) right));
    }};

    public Evaluator(EvaluationContext context) {
        this.context = context;
    }

    @Override
    public void exitStatement(ThoriumParser.StatementContext ctx) {
        context.lastStatementValue = context.popStack();
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        evalOperator("+");
    }

    private void evalOperator(String operator) {
        Value right = context.popStack();
        Value left = context.popStack();

        Operator<Type, Type, Type> op = operators.get(new OperationSignature(operator, left.getType(), right.getType()));

        context.pushStack(op.apply(left.getValue(), right.getValue()));
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        evalOperator("*");
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Value right = context.popStack();
        Value left = context.popStack();

        assertValidAssignment(left, right);

        // TODO refactor without instanceof?
        Symbol value;
        if (left instanceof Constant) {
            value = new Constant(left.getName(), right);
        } else if (left instanceof Variable) {
            value = new Variable(left.getName(), right);
        } else {
            throw new IllegalStateException();
        }

        context.insertSymbol(value);
        context.pushStack(value.getValue());
    }

    private void assertValidAssignment(Value left, Value right) {
        // TODO SEM: move this
        if (!left.isWritable()) {
            throw new InvalidAssignmentTargetException("Cannot assign to " + left.toString());
        }

        if (right.getType() == null) {
            throw new InvalidAssignmentSourceException("Cannot assign from " + right.toString());
        }

        if (!Type.isAssignableFrom(left.getType(), right.getType())) {
            throw new InvalidTypeException(Value.typeName(right) + " is no assignable to " + Value.typeName(left));
        }
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        context.pushStack(new IntegerType(Long.valueOf(ctx.getText())));
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        context.pushStack(new FloatType(Double.valueOf(ctx.getText())));
    }

    @Override
    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        Symbol symbol;

        try {
            symbol = context.lookupSymbol(ctx.getText());
        } catch (SymbolNotFoundException e) {
            symbol = new Variable(ctx.getText()); // TODO EVAL: should be symbol reference instead?
        }

        context.pushStack(symbol);
    }

    @Override
    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        Symbol symbol;

        try {
            symbol = context.lookupSymbol(ctx.getText());
        } catch (SymbolNotFoundException e) {
            symbol = new Constant(ctx.getText()); // TODO EVAL: should be symbol reference instead?
        }

        context.pushStack(symbol);
    }

    @Override
    public void enterBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        context = context.createChild();
    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        context = context.destroyAndRestoreParent();
        context.pushStack(context.lastStatementValue);
    }
}
