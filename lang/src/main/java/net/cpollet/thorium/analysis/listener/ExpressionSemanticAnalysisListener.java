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

package net.cpollet.thorium.analysis.listener;

import net.cpollet.thorium.analysis.AnalysisContext;
import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.analysis.exceptions.InvalidAssignmentException;
import net.cpollet.thorium.analysis.exceptions.InvalidSymbolException;
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public class ExpressionSemanticAnalysisListener extends BaseSemanticAnalysisListener {
    public ExpressionSemanticAnalysisListener(Parser parser, AnalysisContext analysisContext, ParseTreeListener parseTreeListener) {
        super(parser, analysisContext, parseTreeListener);
    }

    @Override
    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        findNodeType(ctx, ctx.literal());
    }

    @Override
    public void exitNotExpression(ThoriumParser.NotExpressionContext ctx) {
        Type type = getNodeType(ctx.expression());

        if (type == Types.NULLABLE_VOID) {
            registerNodeObserver(ctx, ctx.expression());
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), ctx.op.getText(), type);
            context().setTypesOf(ctx, asSet(resultType));
            notifyNodeObservers(ctx);
        }

        // logContextInformation(ctx);
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    private void exitBinaryOperator(String operator, ThoriumParser.ExpressionContext leftExpr, ThoriumParser.ExpressionContext rightExpr, ParserRuleContext ctx) {
        Type leftType = getNodeType(leftExpr);
        Type rightType = getNodeType(rightExpr);

        if (leftType == Types.NULLABLE_VOID) {
            registerNodeObserver(ctx, leftExpr);
        }
        if (rightType == Types.NULLABLE_VOID) {
            registerNodeObserver(ctx, rightExpr);
        }

        if (leftType == Types.NULLABLE_VOID || rightType == Types.NULLABLE_VOID) {
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), operator, leftType, rightType);
            context().setTypesOf(ctx, asSet(resultType));
            notifyNodeObservers(ctx);
        }

        // logContextInformation(ctx);
    }

    private Type inferMethodType(Token token, String methodName, Type leftType, Type... parameterTypes) {
        List<Type> parameterTypesList = Arrays.asList(parameterTypes);

        if (!leftType.isMethodDefined(methodName, parameterTypesList)) {
            context().addException(InvalidSymbolException.methodNotFound(token, methodName, leftType, parameterTypesList));
            return Types.NULLABLE_VOID;
        }

        Method method = leftType.lookupMethod(methodName, parameterTypesList);

        return method.getMethodSignature().getReturnType();
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    @Override
    public void exitOrderComparisonExpression(ThoriumParser.OrderComparisonExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    @Override
    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Type leftType = getNodeType(ctx.identifier());
        Type rightType = getNodeType(ctx.expression());

        Symbol symbol = context().getSymbolTable().lookup(ctx.identifier().getText());

        if (!symbol.isWritable()) {
            context().addException(InvalidAssignmentException.build(ctx.start));
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        symbol.lock();

        if (rightType == Types.NULLABLE_VOID) {
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            registerNodeObserver(ctx, ctx.expression());
            // logContextInformation(ctx);
            return;
        }

        if (leftType == Types.NULLABLE_VOID) {
            symbol.setType(rightType.nullable());
            context().setTypesOf(ctx, asSet(rightType.nullable()));
            notifySymbolObservers(symbol);
            notifyNodeObservers(ctx);
        } else if (rightType.isAssignableTo(leftType)) {
            context().setTypesOf(ctx, asSet(leftType));
            notifySymbolObservers(symbol);
            notifyNodeObservers(ctx);
        } else {
            context().addException(InvalidTypeException.notCompatible(ctx.getStart(), rightType, leftType));
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        }
    }


    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = context().getTypesOf(ctx.block());

        if (possibleTypes.size() > 1) {
            context().addException(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            context().setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        findNodeType(ctx, ctx.block());
    }
}
