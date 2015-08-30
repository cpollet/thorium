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

package ch.pollet.thorium.evaluation;

import ch.pollet.thorium.semantic.exception.SymbolNotFoundException;
import ch.pollet.thorium.values.Symbol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class SymbolTable {
    private final SymbolTable parent;
    private final Map<String, Symbol> symbols;

    public SymbolTable() {
        this.parent = null;
        this.symbols = new HashMap<>();
    }

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public void put(Symbol symbol) {
        SymbolTable table = findTableContaining(symbol.getName());

        if (table == null) {
            table = this;
        }

        table.symbols.put(symbol.getName(), symbol);
    }

    private SymbolTable findTableContaining(String name) {
        if (symbols.containsKey(name)) {
            return this;
        }

        if (parent == null) {
            return null;
        }

        return parent.findTableContaining(name);
    }

    public boolean isDefined(String name) {
        try {
            get(name);
        } catch (SymbolNotFoundException e) {
            return false;
        }

        return true;
    }

    public Symbol get(String name) {
        SymbolTable symbolTable = findTableContaining(name);

        if (symbolTable == null) {
            throw new SymbolNotFoundException(name);
        }

        return symbolTable.symbols.get(name);
    }
}
