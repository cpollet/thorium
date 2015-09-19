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

package ch.pollet.thorium.types;

import ch.pollet.thorium.execution.Method;
import ch.pollet.thorium.execution.MethodMatcher;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class BooleanType extends BaseType {
    static final BooleanType INSTANCE = new BooleanType();

    private static final Map<MethodMatcher, Method> symbolTable = new HashMap<>();

    static {
        symbolTable.put(new MethodMatcher("+", BooleanType.INSTANCE), new Method(BooleanType.INSTANCE, BooleanType::or));
        symbolTable.put(new MethodMatcher("*", BooleanType.INSTANCE), new Method(BooleanType.INSTANCE, BooleanType::and));
        symbolTable.put(new MethodMatcher("!"), new Method(BooleanType.INSTANCE, BooleanType::not));
    }

    private BooleanType() {
        // nothing
    }

    @Override
    Map<MethodMatcher, Method> symbolTable() {
        return symbolTable;
    }

    @Override
    public int id() {
        return ID_BOOLEAN;
    }

    @Override
    public String toString() {
        return "Boolean";
    }

    private static Value or(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Boolean) (left.value().internalValue()) || (Boolean) (right.value().internalValue())
            );
        }

        if (left.hasValue() && (Boolean) (left.value().internalValue())) {
            return left;
        }
        if (right.hasValue() && (Boolean) (right.value().internalValue())) {
            return right;
        }

        return DirectValue.build(Type.BOOLEAN);
    }

    private static Value and(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Boolean) (left.value().internalValue()) && (Boolean) (right.value().internalValue())
            );
        }

        if (left.hasValue() && !(Boolean) (left.value().internalValue())) {
            return left;
        }
        if (right.hasValue() && !(Boolean) (right.value().internalValue())) {
            return right;
        }

        return DirectValue.build(Type.BOOLEAN);
    }

    public static Value not(Value... values) {
        Value value = values[0];

        if (!value.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        return DirectValue.build(!booleanValue(value));
    }
}
