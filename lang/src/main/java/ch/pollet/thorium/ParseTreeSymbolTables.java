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

package ch.pollet.thorium;

import ch.pollet.thorium.analysis.values.Symbol;
import ch.pollet.thorium.execution.SymbolTable;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class ParseTreeSymbolTables {
    protected Map<ParseTree, SymbolTable<Symbol>> symbolTables = new IdentityHashMap<>();

    public SymbolTable<Symbol> get(ParseTree node) {
        return symbolTables.get(node);
    }

    public void put(ParseTree node, SymbolTable<Symbol> value) {
        symbolTables.put(node, value);
    }
}
