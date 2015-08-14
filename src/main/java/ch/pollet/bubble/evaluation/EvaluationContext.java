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

package ch.pollet.bubble.evaluation;

import ch.pollet.bubble.Symbol;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Christophe Pollet
 */
public class EvaluationContext {
    private EvaluationContext parentContext;
    private Map<String, Symbol> symbolsTable;
    private Stack<Value> stack;

    private EvaluationContext() {
        this(null);
    }

    private EvaluationContext(EvaluationContext parentContext) {
        this.stack = new Stack<>();
        this.symbolsTable = new HashMap<>();
        this.parentContext = parentContext;
    }

    public static EvaluationContext createEmpty() {
        return new EvaluationContext();
    }

    public EvaluationContext createChild() {
        return new EvaluationContext(this);
    }

    public Value popStack() {
        return stack.pop();
    }

    public void pushStack(Value object) {
        stack.push(object);
    }

    public void insertSymbol(String name, Symbol symbol) {
        // TODO move this in a semantic checker
        // if (symbolsTable.containsKey(name)) {
        //     throw new SymbolAlreadyDefinedException(name);
        // }
        symbolsTable.put(name, symbol);
    }

    public Symbol lookupSymbol(String name) {
        if (!symbolsTable.containsKey(name)) {
            if (parentContext == null) {
                throw new SymbolNotFoundException(name);
            }
            return parentContext.lookupSymbol(name);
        }

        return symbolsTable.get(name);
    }
}
