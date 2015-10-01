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
import net.cpollet.thorium.analysis.exceptions.InvalidSymbolException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author Christophe Pollet
 */
public class ValuesSemanticAnalysisListener extends BaseSemanticAnalysisListener {
    public ValuesSemanticAnalysisListener(Parser parser, AnalysisContext analysisContext, ParseTreeListener parseTreeListener) {
        super(parser, analysisContext, parseTreeListener);
    }

    @Override
    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        context().setTypesOf(ctx, asSet(Types.BOOLEAN));
        // logContextInformation(ctx);
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        context().setTypesOf(ctx, asSet(Types.INTEGER));
        // logContextInformation(ctx);
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        context().setTypesOf(ctx, asSet(Types.FLOAT));
        // logContextInformation(ctx);
    }

    @Override
    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        findNodeType(ctx, ctx.identifier());
    }

    @Override
    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.LCFirstIdentifier().getText(), Symbol.SymbolKind.VARIABLE);
    }

    private void exitVariableOrConstantName(ParserRuleContext ctx, String name, Symbol.SymbolKind kind) {
        if (!context().getSymbolTable().isDefined(name)) {
            context().addException(InvalidSymbolException.identifierNotFound(ctx.getStart(), name));
            registerSymbol(kind, name, Types.NULLABLE_VOID, ctx);
        }

        Symbol symbol = context().getSymbolTable().lookup(name);
        context().setTypesOf(ctx, asSet(symbol.getType()));

        if (symbol.getType() == Types.NULLABLE_VOID) {
            registerSymbolObserver(ctx, symbol);
        } else {
            notifyNodeObservers(ctx);
        }

        // logContextInformation(ctx);
    }

    @Override
    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.UCIdentifier().getText(), Symbol.SymbolKind.CONSTANT);
    }
}
