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
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author Christophe Pollet
 */
public class StatementsSemanticAnalysisListener extends BaseSemanticAnalysisListener {
    public StatementsSemanticAnalysisListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener,
                                              ObserverRegistry<ParserRuleContext> nodeObserverRegistry,
                                              ObserverRegistry<Symbol> symbolObserverRegistry) {
        super(analysisContext, parseTreeListener, nodeObserverRegistry, symbolObserverRegistry);
    }

    public void enterBlock() {
        wrapSymbolTable();
    }

    public void exitBlock(ThoriumParser.BlockContext ctx) {
        if (ctx.ifStatement() != null) {
            findNodeTypes(ctx, ctx.ifStatement());
        } else if (ctx.statementsBlock() != null) {
            findNodeTypes(ctx, ctx.statementsBlock());
        } else if (ctx.forLoopStatement() != null) {
            findNodeTypes(ctx, ctx.forLoopStatement());
        } else if (ctx.whileLoopStatement() != null) {
            findNodeTypes(ctx, ctx.whileLoopStatement());
        } else {
            throw new IllegalStateException("Unhandled block type");
        }

        unwrapSymbolTable();
    }

    public void exitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        findNodeTypes(ctx, ctx.statements());
    }

    public void exitStatements(ThoriumParser.StatementsContext ctx) {
        findNodeTypes(ctx, ctx.statement(ctx.statement().size() - 1));
    }

    public void exitStatement(ThoriumParser.StatementContext ctx) {
        if (ctx.block() != null) {
            findNodeTypes(ctx, ctx.block());
        } else if (ctx.expressionStatement() != null) {
            findNodeTypes(ctx, ctx.expressionStatement());
        } else if (ctx.variableOrConstantDeclarationStatement() != null) {
            findNodeTypes(ctx, ctx.variableOrConstantDeclarationStatement());
        } else if (";".equals(ctx.getText())) {
            setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            throw new IllegalStateException();
        }
    }

    public void exitVariableDeclarationStatement(ThoriumParser.VariableDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.VARIABLE, ctx.LCFirstIdentifier().getText(), ctx.type(), ctx.expression());

        // logContextInformation(ctx);
    }

    public void exitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.CONSTANT, ctx.UCIdentifier().getText(), ctx.type(), ctx.expression());

        getSymbolTable().lookup(ctx.UCIdentifier().getText()).lock();

        // logContextInformation(ctx);
    }
}
