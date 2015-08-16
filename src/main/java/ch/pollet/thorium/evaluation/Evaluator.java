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

import ch.pollet.thorium.Symbol;
import ch.pollet.thorium.types.FloatType;
import ch.pollet.thorium.types.IntegerType;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.antlr.ThoriumBaseListener;
import ch.pollet.thorium.antlr.ThoriumParser;

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
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        evalOperator("+");
    }

    private void evalOperator(String operator) {
        Value right = context.popStack();
        Value left = context.popStack();

        Type rightValue = right.getValue(context);
        Type leftValue = left.getValue(context);

        Operator<Type, Type, Type> op = operators.get(new OperationSignature(operator, leftValue.getClass(), rightValue.getClass()));

        context.pushStack(op.apply(leftValue, rightValue));
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        evalOperator("*");
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Value right = context.popStack();
        Value left = context.popStack();

        // TODO: move this to a semantic tree walker (as well as type checking and symbol existence checking)
        if (!left.isWritable()) {
            throw new IllegalStateException(left.toString() + " is not writable");
        }

        context.insertSymbol(left.getName(), new Symbol(right, context));
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
    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        context.pushStack(new Identifier(ctx.getText()));
    }
}
