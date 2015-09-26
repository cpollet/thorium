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

package net.cpollet.thorium.execution;

import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.data.method.MethodBody;
import net.cpollet.thorium.data.method.MethodNotFoundException;
import net.cpollet.thorium.data.method.MethodTable;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.execution.values.Symbol;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import net.cpollet.thorium.values.Value;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Christophe Pollet
 */
public class ExecutionContext {
    private final ExecutionContext parentContext;
    private final SymbolTable<Symbol> symbolsTable;
    private final MethodTable methodTable;
    private final Deque<Value> stack;
    private Value lastStatementValue;

    private ExecutionContext() {
        this.stack = new LinkedList<>();
        this.symbolsTable = new SymbolTable<>();
        this.methodTable = new MethodTable();
        this.parentContext = null;
    }

    private ExecutionContext(ExecutionContext parentContext) {
        this.stack = new LinkedList<>();
        this.symbolsTable = new SymbolTable<>(parentContext.symbolsTable);
        this.methodTable = parentContext.methodTable;
        this.parentContext = parentContext;
    }

    public static ExecutionContext createEmpty() {
        return new ExecutionContext();
    }

    public ExecutionContext wrap() {
        return new ExecutionContext(this);
    }

    public ExecutionContext unwrap() {
        parentContext.lastStatementValue = lastStatementValue;

        return parentContext;
    }

    public Value popStack() {
        return stack.pop();
    }

    public void pushStack(Value object) {
        stack.push(object);
    }

    public void updateSymbol(Symbol symbol) {
        symbolsTable.put(symbol.getName(), symbol);
    }

    public void insertSymbol(Symbol symbol) {
        symbolsTable.putInCurrentScope(symbol.getName(), symbol);
    }

    public Symbol lookupSymbol(String name) {
        return symbolsTable.lookup(name);
    }

    public boolean symbolDefined(String name) {
        return symbolsTable.isDefined(name);
    }

    public void insertMethod(String name, MethodBody methodBody, Type targetType, Type returnType, List<Type> parameterTypes, List<String> parameterNames) {
        methodTable.put(name, methodBody, targetType, returnType, parameterTypes, parameterNames);
    }

    public Method lookupMethod(String name, List<Type> parameterTypes) {
        if (!methodDefined(name, parameterTypes)) {
            // TODO SEM implement check in semantic checker
            throw new IllegalStateException("Method not defined");
        }

        return methodTable.lookup(name, Types.VOID, parameterTypes);
    }

    public boolean methodDefined(String name, List<Type> parameterTypes) {
        try {
            methodTable.lookup(name, Types.VOID, parameterTypes);
            return true;
        } catch (MethodNotFoundException e) {
            return false;
        }
    }

    public Value getLastStatementValue() {
        return lastStatementValue;
    }

    public void setLastStatementValue(Value lastStatementValue) {
        this.lastStatementValue = lastStatementValue;
    }
}
