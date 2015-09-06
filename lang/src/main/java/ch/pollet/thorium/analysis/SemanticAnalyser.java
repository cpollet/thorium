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

package ch.pollet.thorium.analysis;

import ch.pollet.thorium.analysis.exceptions.ThoriumSemanticException;
import ch.pollet.thorium.analysis.values.Symbol;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.evaluation.SymbolTable;
import ch.pollet.thorium.types.Type;
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
            throw new ThoriumSemanticException("Semantic errors occurred.", listener.getExceptions());
        }

        return listener.getTypes();
    }
}
