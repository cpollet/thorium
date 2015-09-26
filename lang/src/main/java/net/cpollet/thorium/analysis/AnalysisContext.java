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

import net.cpollet.thorium.ThoriumException;
import net.cpollet.thorium.analysis.values.Symbol;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.types.Type;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public class AnalysisContext {
    private final ParseTreeTypes types = new ParseTreeTypes();
    private final ParseTreeSymbolTables symbolTables = new ParseTreeSymbolTables();
    private final List<Symbol> symbols = new LinkedList<>();
    private final List<ThoriumException> exceptions = new ArrayList<>();

    private SymbolTable<Symbol> currentSymbolTable;

    public AnalysisContext(SymbolTable<Symbol> currentSymbolTable) {
        this.currentSymbolTable = currentSymbolTable;
    }

    public Set<Type> getTypesOf(ParseTree ctx) {
        return types.get(ctx);
    }

    public void setTypesOf(ParseTree ctx, Set<Type> types) {
        this.types.put(ctx, types);
    }

    public ParseTreeProperty<Type> getTypesOfAllNodes() {
        return types.reduce();
    }

    public SymbolTable<Symbol> getSymbolTable() {
        return currentSymbolTable;
    }

    public SymbolTable<Symbol> getSymbolTable(ParseTree ctx) {
        return symbolTables.get(ctx);
    }

    // TODO OPTIMIZE don't store if same as parent
    public void storeSymbolTable(ParseTree ctx) {
        if (symbolTables.get(ctx) == null) {
            symbolTables.put(ctx, currentSymbolTable);
        }
    }

    public void wrapSymbolTable() {
        currentSymbolTable = currentSymbolTable.wrap();
    }

    public void unwrapSymbolTable() {
        currentSymbolTable = currentSymbolTable.unwrap();
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void addSymbol(Symbol symbol) {
        symbols.add(symbol);
    }

    public void addException(ThoriumException exception) {
        exceptions.add(exception);
    }

    public List<ThoriumException> getExceptions() {
        return exceptions;
    }
}
