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
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.method.ParameterSignature;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class StatementsListener extends BaseListener {
    public StatementsListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener, Observers observers) {
        super(analysisContext, parseTreeListener, observers);
    }

    public void enterBlock() {
        wrapSymbolTable();
    }

    public void exitBlock(ThoriumParser.BlockContext ctx) {
        if (ctx.ifStatement() != null) {
            inferNodeTypes(ctx, ctx.ifStatement());
        } else if (ctx.statementsBlock() != null) {
            inferNodeTypes(ctx, ctx.statementsBlock());
        } else if (ctx.forLoopStatement() != null) {
            inferNodeTypes(ctx, ctx.forLoopStatement());
        } else if (ctx.whileLoopStatement() != null) {
            inferNodeTypes(ctx, ctx.whileLoopStatement());
        } else {
            throw new IllegalStateException("Unhandled block type");
        }

        unwrapSymbolTable();
    }

    public void exitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        inferNodeTypes(ctx, ctx.statements());
    }

    public void exitStatements(ThoriumParser.StatementsContext ctx) {
        inferNodeTypes(ctx, ctx.statement(ctx.statement().size() - 1));
    }

    public void exitStatement(ThoriumParser.StatementContext ctx) {
        if (ctx.block() != null) {
            inferNodeTypes(ctx, ctx.block());
        } else if (ctx.expressionStatement() != null) {
            inferNodeTypes(ctx, ctx.expressionStatement());
        } else if (ctx.variableOrConstantDeclarationStatement() != null) {
            inferNodeTypes(ctx, ctx.variableOrConstantDeclarationStatement());
        } else if (ctx.methodDefinition() != null || ";".equals(ctx.getText())) {
            setNodeTypes(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            throw new IllegalStateException();
        }
    }

    public void exitVariableDeclarationStatement(ThoriumParser.VariableDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.VARIABLE, ctx.LCFirstIdentifier().getText(), ctx.type(), ctx.expression());
    }

    public void exitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.CONSTANT, ctx.UCIdentifier().getText(), ctx.type(), ctx.expression());

        getSymbolTable().lookup(ctx.UCIdentifier().getText()).lock();
    }

    public void exitMethodDefinition(ThoriumParser.MethodDefinitionContext ctx) {
        String methodName = ctx.methodName().getText();
        Type returnType = getNodeType(ctx.type());
        List<ParameterSignature> formalParameters = extractParameterSignatures(ctx);

        getMethodTable().put(methodName, null, Types.VOID, returnType, formalParameters);

        notifyMethodObservers(methodName);
    }

    private List<ParameterSignature> extractParameterSignatures(ThoriumParser.MethodDefinitionContext ctx) {
        if (ctx.formalParameters() == null) {
            return Collections.emptyList();
        }

        return ctx.formalParameters().formalParameter().stream()
                .map(e -> new ParameterSignature(getNodeType(e.type()), e.LCFirstIdentifier().getText()))
                .collect(Collectors.toList());
    }
}
