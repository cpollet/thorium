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

package ch.pollet.thorium.execution;

import ch.pollet.thorium.execution.values.Symbol;
import ch.pollet.thorium.values.Value;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

/**
 * @author Christophe Pollet
 */
public class ExecutionContext {
    private final ExecutionContext parentContext;
    private final SymbolTable<Symbol> symbolsTable;
    private final Deque<Value> stack;
    private Value lastStatementValue;

    private ExecutionContext() {
        this.stack = new LinkedList<>();
        this.symbolsTable = new SymbolTable<>();
        this.parentContext = null;
    }

    private ExecutionContext(ExecutionContext parentContext) {
        this.stack = new LinkedList<>();
        this.symbolsTable = new SymbolTable<>(parentContext.symbolsTable);
        this.parentContext = parentContext;
    }

    public static ExecutionContext createEmpty() {
        return new ExecutionContext();
    }

    public ExecutionContext createChild() {
        return new ExecutionContext(this);
    }

    public ExecutionContext destroyAndRestoreParent() {
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
        symbolsTable.put(symbol.getName(), symbol);
    }

    public Symbol lookupSymbol(String name) {
        return symbolsTable.get(name);
    }

    public boolean symbolDefined(String name){
        return symbolsTable.isDefined(name);
    }

    public Value getLastStatementValue() {
        return lastStatementValue;
    }

    public void setLastStatementValue(Value lastStatementValue) {
        this.lastStatementValue = lastStatementValue;
    }
}
