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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author Christophe Pollet
 */
public class ValuesListener extends BaseListener {
    public ValuesListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener, Observers observers) {
        super(analysisContext, parseTreeListener, observers);
    }

    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        setNodeTypes(ctx, asSet(Types.BOOLEAN));
    }

    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        setNodeTypes(ctx, asSet(Types.INTEGER));
    }

    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        setNodeTypes(ctx, asSet(Types.FLOAT));
    }

    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        inferNodeType(ctx, ctx.identifier());
    }

    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.LCFirstIdentifier().getText(), Symbol.SymbolKind.VARIABLE);
    }

    private void exitVariableOrConstantName(ParserRuleContext ctx, String name, Symbol.SymbolKind kind) {
        if (!getSymbolTable().isDefined(name)) {
            addException(InvalidSymbolException.identifierNotFound(ctx.getStart(), name));
            registerSymbol(kind, name, Types.NULLABLE_VOID, ctx);
        }

        Symbol symbol = getSymbolTable().lookup(name);
        setNodeTypes(ctx, asSet(symbol.getType()));

        if (symbol.getType() == Types.NULLABLE_VOID) {
            registerSymbolObserver(ctx, symbol);
        } else {
            notifyNodeObservers(ctx);
        }
    }

    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.UCIdentifier().getText(), Symbol.SymbolKind.CONSTANT);
    }
}
