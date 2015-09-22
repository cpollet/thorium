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

import net.cpollet.thorium.analysis.exceptions.ThoriumSemanticException;
import net.cpollet.thorium.analysis.values.Symbol;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.types.Type;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author Christophe Pollet
 */
public class SemanticAnalyser {
    private final SymbolTable<Symbol> scope;
    private final ParseTree tree;
    private final ThoriumParser parser;

    public SemanticAnalyser(SymbolTable<Symbol> scope, ThoriumParser parser, ParseTree tree) {
        this.scope = scope;
        this.parser = parser;
        this.tree = tree;
    }

    public ParseTreeProperty<Type> analyze() {
        ParseTreeWalker walker = new ParseTreeWalker();
        SemanticAnalysisListener listener = new SemanticAnalysisListener(parser, scope);

        walker.walk(listener, tree);

        if (!listener.getExceptions().isEmpty()) {
            throw new ThoriumSemanticException(listener.getExceptions().size() + " semantic errors occurred.", listener.getExceptions());
        }

        return listener.getTypes();
    }
}
