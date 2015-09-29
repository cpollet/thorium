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
import net.cpollet.thorium.data.method.MethodEvaluationContext;
import net.cpollet.thorium.data.method.MethodSignature;
import net.cpollet.thorium.data.method.ParameterSignature;
import net.cpollet.thorium.execution.data.method.NonNativeMethodBody;
import net.cpollet.thorium.execution.values.Symbol;
import net.cpollet.thorium.execution.values.Variable;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import net.cpollet.thorium.values.DirectValue;
import net.cpollet.thorium.values.Value;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
        createSymbolInCurrentScope(ctx.LCFirstIdentifier().getText(), ctx.expression());

        return null;
    }

    @Override
    public Void visitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        createSymbolInCurrentScope(ctx.UCIdentifier().getText(), ctx.expression());

        return null;
    }

    private Symbol createSymbolInCurrentScope(String identifier, ThoriumParser.ExpressionContext exprCtx) {
        if (!context.symbolDefinedInCurrentScope(identifier)) {
            Symbol symbol = new Variable(identifier); // TODO EVAL: should be symbol reference instead? -> yes it probably eases the thing with def, etc.
            updateValue(symbol, exprCtx);

            context.insertSymbol(symbol);

            return symbol;
        }

        return context.lookupSymbol(identifier);
    }

    private void updateValue(Symbol symbol, ThoriumParser.ExpressionContext exprCtx) {
        if (exprCtx != null) {
            visit(exprCtx);
            symbol.setValue(context.popStack().value());
            symbol.setType(symbol.value().type());
        }
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
        context = context.wrap();

        visitStatements(ctx);

        context = context.unwrap();
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

        Method method = left.type().lookupMethod(operator, Collections.singletonList(right.type()));

        context.pushStack(method.apply(new MethodEvaluationContext(context, left, right)));
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

        Method method = value.type().lookupMethod(operator, Collections.emptyList());

        context.pushStack(method.apply(new MethodEvaluationContext(context, value)));
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

    @Override
    public Void visitMethodCallExpression(ThoriumParser.MethodCallExpressionContext ctx) {
        String methodName = ctx.methodName().LCFirstIdentifier().getText();
        List<Value> parameterValues = evalParametersValues(ctx.parameters());

        Method method = context.lookupMethod(methodName, parameterValues.stream().map(Value::type).collect(Collectors.toList()));
        MethodSignature signature = method.getMethodSignature();

        context = context.wrap();

        for (int i = 0; i < parameterValues.size(); i++) {
            context.insertSymbol(new Variable(signature.getParameterName(i), parameterValues.get(i).value()));
        }

        Value returnValue = method.apply(new MethodEvaluationContext(context, parameterValues));

        context = context.unwrap();

        context.pushStack(returnValue);

        return null;
    }

    private List<Value> evalParametersValues(ThoriumParser.ParametersContext parameters) {
        if (parameters == null || parameters.expression().isEmpty()) {
            return Collections.emptyList();
        }

        List<Value> values = new LinkedList<>();

        for (ThoriumParser.ExpressionContext expression : parameters.expression()) {
            visit(expression);
            Value value = context.popStack();
            values.add(value);
        }

        return values;
    }

    //endregion

    //region If Statement

    @Override
    public Void visitIfStatement(ThoriumParser.IfStatementContext ctx) {
        context = context.wrap();

        visitNestedIfStatement(ctx);

        context = context.unwrap();

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
        context = context.wrap();

        while (isExpressionTrue(ctx.expression())) {
            visit(ctx.statements());
        }

        context = context.unwrap();

        return null;
    }

    @Override
    public Void visitForLoopStatement(ThoriumParser.ForLoopStatementContext ctx) {
        context = context.wrap();

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

        context = context.unwrap();

        return null;
    }

    @Override
    public Void visitForLoopStatementInitExpression(ThoriumParser.ForLoopStatementInitExpressionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Void visitForLoopStatementInitVariableDeclaration(ThoriumParser.ForLoopStatementInitVariableDeclarationContext ctx) {
        Symbol symbol = createSymbolInCurrentScope(ctx.LCFirstIdentifier().getText(), ctx.expression());
        context.pushStack(symbol.value());

        return null;
    }

    //endregion

    @Override
    public Void visitMethodDefinition(ThoriumParser.MethodDefinitionContext ctx) {
        List<ParameterSignature> parameterSignatures = Collections.emptyList();
        ThoriumParser.FormalParametersContext formalParametersCtx = ctx.formalParameters();

        if (formalParametersCtx != null) {
            parameterSignatures = formalParametersCtx.formalParameter().stream()
                    .map(parameterCtx -> new ParameterSignature(decode(parameterCtx.type()), parameterCtx.LCFirstIdentifier().getText()))
                    .collect(Collectors.toList());
        }

        NonNativeMethodBody methodBody = new NonNativeMethodBody(ctx.statements());

        context.insertMethod(ctx.methodName().getText(), methodBody, Types.VOID, Types.VOID, parameterSignatures);

        return null;
    }

    // TODO DESIGN probably not the best way of decoding types ;)
    private static Type decode(ThoriumParser.TypeContext typeCtx) {
        Type type;
        switch (typeCtx.UCFirstIdentifier().getText()) {
            case "Integer":
                type = Types.INTEGER;
                break;
            case "Float":
                type = Types.FLOAT;
                break;
            case "Boolean":
                type = Types.BOOLEAN;
                break;
            case "Void":
                type = Types.VOID;
                break;
            default:
                throw new IllegalStateException("Type " + typeCtx.UCFirstIdentifier().getText() + " not supported.");
        }

        if (typeCtx.nullable != null) {
            type = type.nullable();
        }

        return type;
    }


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
        Symbol symbol = createOrUpdateSymbol(ctx.LCFirstIdentifier().getText(), null);

        context.pushStack(symbol);

        return null;
    }

    private Symbol createOrUpdateSymbol(String identifier, ThoriumParser.ExpressionContext exprCtx) {
        if (!context.symbolDefined(identifier)) {
            Symbol symbol = new Variable(identifier); // TODO EVAL: should be symbol reference instead? -> yes it probably eases the thing with def, etc.
            updateValue(symbol, exprCtx);

            context.updateOrInsertSymbol(symbol);

            return symbol;
        }

        return context.lookupSymbol(identifier);
    }

    @Override
    public Void visitConstantName(ThoriumParser.ConstantNameContext ctx) {
        Symbol symbol = createOrUpdateSymbol(ctx.UCIdentifier().getText(), null);

        context.pushStack(symbol);

        return null;
    }

    //endregion
}
