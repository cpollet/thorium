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

package ch.pollet.bubble.evaluation;

import ch.pollet.bubble.Symbol;
import ch.pollet.bubble.antlr.BubbleBaseListener;
import ch.pollet.bubble.antlr.BubbleParser;
import ch.pollet.bubble.types.FloatType;
import ch.pollet.bubble.types.IntegerType;
import ch.pollet.bubble.types.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class Evaluator extends BubbleBaseListener {
    private EvaluationContext evaluationContext;

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

    public Evaluator(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public void exitAdditionExpression(BubbleParser.AdditionExpressionContext ctx) {
        evalOperator("+");
    }

    private void evalOperator(String operator) {
        Type right = (Type) evaluationContext.popStack();
        Type left = (Type) evaluationContext.popStack();

        Operator<Type, Type, Type> op = operators.get(new OperationSignature(operator, left.getClass(), right.getClass()));

        evaluationContext.pushStack(op.apply(left, right));
    }

    @Override
    public void exitMultiplicationExpression(BubbleParser.MultiplicationExpressionContext ctx) {
        evalOperator("*");
    }

    @Override
    public void exitAssignmentExpression(BubbleParser.AssignmentExpressionContext ctx) {
        Value right = evaluationContext.popStack();
        Value left = evaluationContext.popStack();

        // TODO: move this to a semantic tree walker (as well as type checking and symbol existence checking)
        if (!left.isWritable()) {
            throw new IllegalStateException(left.toString() + " is not writable");
        }

        evaluationContext.insertSymbol(left.getName(), new Symbol(right, evaluationContext));
    }

    @Override
    public void exitIntegerLiteral(BubbleParser.IntegerLiteralContext ctx) {
        evaluationContext.pushStack(new IntegerType(Long.valueOf(ctx.getText())));
    }

    @Override
    public void exitFloatLiteral(BubbleParser.FloatLiteralContext ctx) {
        evaluationContext.pushStack(new FloatType(Double.valueOf(ctx.getText())));
    }

    @Override
    public void exitIdentifierLiteral(BubbleParser.IdentifierLiteralContext ctx) {
        evaluationContext.pushStack(new Identifier(ctx.getText()));
    }
}
