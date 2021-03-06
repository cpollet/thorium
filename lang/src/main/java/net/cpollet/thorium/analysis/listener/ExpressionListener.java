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
import net.cpollet.thorium.data.method.MethodNotFoundException;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class ExpressionListener extends BaseListener {
    public ExpressionListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener, Observers observers) {
        super(analysisContext, parseTreeListener, observers);
    }

    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        inferNodeType(ctx, ctx.literal());
    }

    public void exitNotExpression(ThoriumParser.NotExpressionContext ctx) {
        Type type = getNodeType(ctx.expression());

        if (type == Types.NULLABLE_VOID) {
            registerNodeObserver(ctx, ctx.expression());
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), ctx.op.getText(), type);
            setNodeTypes(ctx, asSet(resultType));
            notifyNodeObservers(ctx);
        }
    }

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
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), operator, leftType, rightType);
            setNodeTypes(ctx, asSet(resultType));
            notifyNodeObservers(ctx);
        }
    }

    private Type inferMethodType(Token token, String methodName, Type leftType, Type... parameterTypes) {
        List<Type> parameterTypesList = Arrays.asList(parameterTypes);

        if (!leftType.isMethodDefined(methodName, parameterTypesList)) {
            addException(InvalidSymbolException.methodNotFound(token, methodName, leftType, parameterTypesList));
            return Types.NULLABLE_VOID;
        }

        Method method = leftType.lookupMethod(methodName, parameterTypesList);

        return method.getMethodSignature().getReturnType();
    }

    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    public void exitOrderComparisonExpression(ThoriumParser.OrderComparisonExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        inferNodeType(ctx, ctx.expression());
    }

    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Type leftType = getNodeType(ctx.identifier());
        Type rightType = getNodeType(ctx.expression());

        Symbol symbol = getSymbolTable().lookup(ctx.identifier().getText());

        if (!symbol.isWritable()) {
            addException(InvalidAssignmentException.build(ctx.start));
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        symbol.lock();

        if (rightType == Types.NULLABLE_VOID) {
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
            registerNodeObserver(ctx, ctx.expression());
            return;
        }

        if (leftType == Types.NULLABLE_VOID) {
            symbol.setType(rightType.nullable());
            setNodeTypes(ctx, asSet(rightType.nullable()));
            notifySymbolObservers(symbol);
            notifyNodeObservers(ctx);
        } else if (rightType.isAssignableTo(leftType)) {
            setNodeTypes(ctx, asSet(leftType));
            notifySymbolObservers(symbol);
            notifyNodeObservers(ctx);
        } else {
            addException(InvalidTypeException.notCompatible(ctx.getStart(), rightType, leftType));
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
        }
    }


    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = getNodeTypes(ctx.block());

        if (possibleTypes.size() > 1) {
            addException(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        inferNodeType(ctx, ctx.block());
    }

    public void exitMethodCallExpression(ThoriumParser.MethodCallExpressionContext ctx) {
        List<Type> parameterTypes = extractParameterTypes(ctx);

        boolean someParameterTypesAreNotResolved = parameterTypes.stream()
                .anyMatch(e -> e.nonNullable() == Types.VOID);

        if (someParameterTypesAreNotResolved) {
            setNodeTypes(ctx, asSet(Types.VOID));
            return;
        }

        String methodName = ctx.methodName().getText();
        try {
            Method method = getMethodTable().lookup(methodName, Types.VOID, parameterTypes);

            setNodeTypes(ctx, asSet(method.getMethodSignature().getReturnType()));
            notifyNodeObservers(ctx);
        } catch (MethodNotFoundException e) {
            registerMethodObserver(ctx, methodName);
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
        }
    }

    private List<Type> extractParameterTypes(ThoriumParser.MethodCallExpressionContext ctx) {
        if (ctx.parameters() == null) {
            return Collections.emptyList();
        }

        ctx.parameters().expression().stream()
                .filter(expr -> getNodeType(expr).nonNullable() == Types.VOID)
                .forEach(expressionContext -> registerNodeObserver(ctx, expressionContext));

        return ctx.parameters().expression().stream()
                .map(this::getNodeType)
                .collect(Collectors.toList());
    }
}
