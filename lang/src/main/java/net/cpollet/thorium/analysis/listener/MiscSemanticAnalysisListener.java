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
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author Christophe Pollet
 */
public class MiscSemanticAnalysisListener extends BaseSemanticAnalysisListener {
    public MiscSemanticAnalysisListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener,
                                        ObserverRegistry<ParserRuleContext> nodeObserverRegistry,
                                        ObserverRegistry<Symbol> symbolObserverRegistry) {
        super(analysisContext, parseTreeListener, nodeObserverRegistry, symbolObserverRegistry);
    }

    public void enterEveryRule(ParserRuleContext ctx) {
        storeSymbolTable(ctx);
    }

    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        //noinspection Convert2streamapi
        for (Symbol symbol : getSymbols()) {
            if (symbol.getType() == Types.NULLABLE_VOID) {
                addException(InvalidTypeException.typeExpected(symbol.getToken()));
            }
        }
    }

    // TODO not the best way to deduce types...
    public void exitType(ThoriumParser.TypeContext ctx) {
        Type.Nullable nullable = ctx.nullable != null ? Type.Nullable.YES : Type.Nullable.NO;

        switch (ctx.UCFirstIdentifier().getText()) {
            case "Integer":
                setTypesOf(ctx, asSet(Types.get(Types.INTEGER, nullable)));
                break;
            case "Float":
                setTypesOf(ctx, asSet(Types.get(Types.FLOAT, nullable)));
                break;
            case "Boolean":
                setTypesOf(ctx, asSet(Types.get(Types.BOOLEAN, nullable)));
                break;
            default:
                throw new IllegalStateException("Invalid type");
        }
    }
}
