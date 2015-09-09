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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class SymbolTable<T> {
    private final SymbolTable<T> parent;
    private final Map<String, T> symbols;

    public SymbolTable() {
        this.parent = null;
        this.symbols = new HashMap<>();
    }

    public SymbolTable(SymbolTable<T> parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public void put(String name, T symbol) {
        SymbolTable<T> table = findTableContaining(name);

        if (table == null) {
            table = this;
        }

        table.symbols.put(name, symbol);
    }

    public SymbolTable<T> unwrap() {
        if (parent == null) {
            throw new IllegalStateException("Called unwrap on root symbol table.");
        }

        return parent;
    }

    private SymbolTable<T> findTableContaining(String name) {
        if (symbols.containsKey(name)) {
            return this;
        }

        if (parent == null) {
            return null;
        }

        return parent.findTableContaining(name);
    }

    public boolean isDefined(String name) {
        SymbolTable<T> table = findTableContaining(name);

        return table != null;
    }

    public boolean isDefinedInCurrentScope(String name) {
        return symbols.containsKey(name);
    }

    public T get(String name) {
        SymbolTable<T> symbolTable = findTableContaining(name);

        if (symbolTable == null) {
            throw new IllegalStateException("Symbol " + name + " is not defined");
        }

        return symbolTable.symbols.get(name);
    }
}
