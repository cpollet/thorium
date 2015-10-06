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
import net.cpollet.thorium.analysis.ObserverRegistry;
import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.antlr.ThoriumBaseListener;
import net.cpollet.thorium.antlr.ThoriumParser;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * @author Christophe Pollet
 */
public class SemanticAnalysisListener extends ThoriumBaseListener {
    private final StatementsListener statementsListener;
    private final ControlStatementsListener controlStatementListener;
    private final ExpressionListener expressionListener;
    private final ConditionalStatementsListener conditionalStatementsListener;
    private final ValuesListener valuesListener;
    private final MiscListener miscListener;

    public SemanticAnalysisListener(AnalysisContext context) {
        ObserverRegistry<ParserRuleContext> nodeObserverRegistry = new ObserverRegistry<>();
        ObserverRegistry<Symbol> symbolObserverRegistry = new ObserverRegistry<>();
        ObserverRegistry<String> methodObserverRegistry = new ObserverRegistry<>();

        BaseListener.Observers observers = new BaseListener.Observers(nodeObserverRegistry, symbolObserverRegistry, methodObserverRegistry);

        controlStatementListener = new ControlStatementsListener(context, this, observers);
        statementsListener = new StatementsListener(context, this, observers);
        expressionListener = new ExpressionListener(context, this, observers);
        conditionalStatementsListener = new ConditionalStatementsListener(context, this, observers);
        valuesListener = new ValuesListener(context, this, observers);
        miscListener = new MiscListener(context, this, observers);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        miscListener.enterEveryRule(ctx);
    }

    @Override
    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        miscListener.exitCompilationUnit();
    }

    @Override
    public void exitType(ThoriumParser.TypeContext ctx) {
        miscListener.exitType(ctx);
    }

    //region Statements

    @Override
    public void enterBlock(ThoriumParser.BlockContext ctx) {
        statementsListener.enterBlock();
    }

    @Override
    public void exitBlock(ThoriumParser.BlockContext ctx) {
        statementsListener.exitBlock(ctx);
    }

    @Override
    public void exitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        statementsListener.exitStatementsBlock(ctx);
    }

    @Override
    public void exitStatements(ThoriumParser.StatementsContext ctx) {
        statementsListener.exitStatements(ctx);
    }

    @Override
    public void exitStatement(ThoriumParser.StatementContext ctx) {
        statementsListener.exitStatement(ctx);
    }

    @Override
    public void exitVariableDeclarationStatement(ThoriumParser.VariableDeclarationStatementContext ctx) {
        statementsListener.exitVariableDeclarationStatement(ctx);
    }

    @Override
    public void exitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        statementsListener.exitConstantDeclarationStatement(ctx);
    }

    //endregion

    //region If Statement

    @Override
    public void enterIfStatement(ThoriumParser.IfStatementContext ctx) {
        controlStatementListener.enterIfStatement();
    }

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        controlStatementListener.exitIfStatement(ctx);
    }

    @Override
    public void enterElseStatement(ThoriumParser.ElseStatementContext ctx) {
        controlStatementListener.enterElseStatement();
    }

    @Override
    public void exitElseStatement(ThoriumParser.ElseStatementContext ctx) {
        controlStatementListener.exitElseStatement(ctx);
    }

    //endregion

    //region Loop Statements

    @Override
    public void exitForLoopStatement(ThoriumParser.ForLoopStatementContext ctx) {
        controlStatementListener.exitForLoopStatement(ctx);
    }

    @Override
    public void exitForLoopStatementInitVariableDeclaration(ThoriumParser.ForLoopStatementInitVariableDeclarationContext ctx) {
        controlStatementListener.exitForLoopStatementInitVariableDeclaration(ctx);
    }

    @Override
    public void exitWhileLoopStatement(ThoriumParser.WhileLoopStatementContext ctx) {
        controlStatementListener.exitWhileLoopStatement(ctx);
    }

    //endregion

    //region Conditional statements

    @Override
    public void exitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        conditionalStatementsListener.exitUnconditionalStatement(ctx);
    }

    @Override
    public void exitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        conditionalStatementsListener.exitConditionalIfStatement(ctx);
    }

    @Override
    public void exitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        conditionalStatementsListener.exitConditionalUnlessStatement(ctx);
    }

    @Override
    public void exitRepeatedWhileStatement(ThoriumParser.RepeatedWhileStatementContext ctx) {
        conditionalStatementsListener.exitRepeatedWhileStatement(ctx);
    }

    @Override
    public void exitRepeatedUntilStatement(ThoriumParser.RepeatedUntilStatementContext ctx) {
        conditionalStatementsListener.exitRepeatedUntilStatement(ctx);
    }

    //endregion

    //region Expressions

    @Override
    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        expressionListener.exitLiteralExpression(ctx);
    }

    @Override
    public void exitNotExpression(ThoriumParser.NotExpressionContext ctx) {
        expressionListener.exitNotExpression(ctx);
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        expressionListener.exitMultiplicationExpression(ctx);
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        expressionListener.exitAdditionExpression(ctx);
    }

    @Override
    public void exitOrderComparisonExpression(ThoriumParser.OrderComparisonExpressionContext ctx) {
        expressionListener.exitOrderComparisonExpression(ctx);
    }

    @Override
    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        expressionListener.exitParenthesisExpression(ctx);
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        expressionListener.exitAssignmentExpression(ctx);
    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        expressionListener.exitBlockExpression(ctx);
    }

    @Override
    public void exitMethodCallExpression(ThoriumParser.MethodCallExpressionContext ctx) {
        expressionListener.exitMethodCallExpression(ctx);
    }

    //endregion

    //region Values

    @Override
    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        valuesListener.exitBooleanLiteral(ctx);
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        valuesListener.exitIntegerLiteral(ctx);
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        valuesListener.exitFloatLiteral(ctx);
    }

    @Override
    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        valuesListener.exitIdentifierLiteral(ctx);
    }

    @Override
    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        valuesListener.exitVariableName(ctx);
    }

    @Override
    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        valuesListener.exitConstantName(ctx);
    }

    //endregion

    @Override
    public void exitMethodDefinition(ThoriumParser.MethodDefinitionContext ctx) {
        statementsListener.exitMethodDefinition(ctx);// TODO move this in another class
    }
}
