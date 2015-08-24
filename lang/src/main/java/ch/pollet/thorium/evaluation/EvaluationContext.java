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

import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;
import ch.pollet.thorium.values.types.Type;

import java.util.Stack;

/**
 * @author Christophe Pollet
 */
public class EvaluationContext {
    private final EvaluationContext parentContext;
    private final SymbolTable symbolsTable;
    private final Stack<Value> stack;
    public Value lastStatementValue;

    private EvaluationContext() {
        this.stack = new Stack<>();
        this.symbolsTable = new SymbolTable();
        this.parentContext = null;
    }

    private EvaluationContext(EvaluationContext parentContext) {
        this.stack = new Stack<>();
        this.symbolsTable = new SymbolTable(parentContext.symbolsTable);
        this.parentContext = parentContext;
    }

    public static EvaluationContext createEmpty() {
        return new EvaluationContext();
    }

    public EvaluationContext createChild() {
        return new EvaluationContext(this);
    }

    public EvaluationContext destroyAndRestoreParent() {
        parentContext.lastStatementValue = lastStatementValue;

        return parentContext;
    }

    public Value popStack() {
        return stack.pop();
    }

    public void pushStack(Value object) {
        stack.push(object);
    }

    public void insertSymbol(Symbol symbol) {
        // TODO SEM: check symbol does not already exists
        symbolsTable.put(symbol);
    }

    public Symbol lookupSymbol(String name) {
        return symbolsTable.get(name);
    }
}
