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

package net.cpollet.thorium.jbehave;

import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.execution.ExecutionContext;
import net.cpollet.thorium.types.Type;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * @author Christophe Pollet
 */
public class JBehaveStoryContext {
    public ParseTree tree;
    public ExecutionContext executionContext;
    public Throwable exception;
    public boolean exceptionExpected;
    public ThoriumParser parser;
    public SymbolTable<net.cpollet.thorium.execution.values.Symbol> evaluationBaseScope;
    public SymbolTable<net.cpollet.thorium.analysis.values.Symbol> analysisBaseScope;
    public ParseTreeProperty<Type> types;
    public String thoriumCode;
    public int exceptionsExpected;
}
