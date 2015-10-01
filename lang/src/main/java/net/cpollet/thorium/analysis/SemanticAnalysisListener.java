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

package net.cpollet.thorium.analysis;

import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.analysis.exceptions.InvalidSymbolException;
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.analysis.listener.ConditionalStatementsSemanticAnalysisListener;
import net.cpollet.thorium.analysis.listener.ControlStatementsSemanticAnalysisListener;
import net.cpollet.thorium.analysis.listener.ExpressionSemanticAnalysisListener;
import net.cpollet.thorium.analysis.listener.MiscSemanticAnalysisListener;
import net.cpollet.thorium.analysis.listener.StatementsSemanticAnalysisListener;
import net.cpollet.thorium.analysis.listener.ValuesSemanticAnalysisListener;
import net.cpollet.thorium.antlr.ThoriumBaseListener;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public class SemanticAnalysisListener extends ThoriumBaseListener {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticAnalysisListener.class);

    private final List<String> ruleNames;

    private final AnalysisContext analysisContext;

    private final StatementsSemanticAnalysisListener statementsListener;
    private final ControlStatementsSemanticAnalysisListener controlStatementListener;
    private final ExpressionSemanticAnalysisListener expressionListener;
    private final ConditionalStatementsSemanticAnalysisListener conditionalStatementsListener;
    private final ValuesSemanticAnalysisListener valuesListener;
    private final MiscSemanticAnalysisListener miscListener;

    public SemanticAnalysisListener(Parser parser, AnalysisContext analysisContext) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
        this.analysisContext = analysisContext;

        ObserverRegistry<ParserRuleContext> nodeObserverRegistry = new ObserverRegistry<>();
        ObserverRegistry<Symbol> symbolObserverRegistry = new ObserverRegistry<>();

        this.controlStatementListener = new ControlStatementsSemanticAnalysisListener(parser, analysisContext, this);
        this.controlStatementListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.controlStatementListener.setSymbolObserverRegistry(symbolObserverRegistry);

        this.statementsListener = new StatementsSemanticAnalysisListener(parser, analysisContext, this);
        this.statementsListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.statementsListener.setSymbolObserverRegistry(symbolObserverRegistry);

        this.expressionListener = new ExpressionSemanticAnalysisListener(parser, analysisContext, this);
        this.expressionListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.expressionListener.setSymbolObserverRegistry(symbolObserverRegistry);

        this.conditionalStatementsListener = new ConditionalStatementsSemanticAnalysisListener(parser, analysisContext, this);
        this.conditionalStatementsListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.conditionalStatementsListener.setSymbolObserverRegistry(symbolObserverRegistry);

        this.valuesListener = new ValuesSemanticAnalysisListener(parser, analysisContext, this);
        this.valuesListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.valuesListener.setSymbolObserverRegistry(symbolObserverRegistry);

        this.miscListener = new MiscSemanticAnalysisListener(parser, analysisContext, this);
        this.miscListener.setNodeObserverRegistry(nodeObserverRegistry);
        this.miscListener.setSymbolObserverRegistry(symbolObserverRegistry);
    }

    public AnalysisResult getResult() {
        return new AnalysisResult(analysisContext.getTypesOfAllNodes(), analysisContext.getExceptions());
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        miscListener.enterEveryRule(ctx);
    }

    @Override
    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        miscListener.exitCompilationUnit(ctx);
    }

    @Override
    public void exitType(ThoriumParser.TypeContext ctx) {
        miscListener.exitType(ctx);
    }

    //region Statements

    @Override
    public void enterBlock(ThoriumParser.BlockContext ctx) {
        statementsListener.enterBlock(ctx);
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
        controlStatementListener.enterIfStatement(ctx);
    }

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        controlStatementListener.exitIfStatement(ctx);
    }

    @Override
    public void enterElseStatement(ThoriumParser.ElseStatementContext ctx) {
        controlStatementListener.enterElseStatement(ctx);
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
        // exitVariableOrConstantName(ctx, ctx.UCIdentifier().getText(), Symbol.SymbolKind.CONSTANT);
    }

    //endregion

    private static Set<Type> asSet(Type... types) {
        return new HashSet<>(Arrays.asList(types));
    }

}
