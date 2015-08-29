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

import ch.pollet.thorium.evaluation.Method;
import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class FloatType extends BaseType {
    static final FloatType INSTANCE = new FloatType();

    private static final Map<MethodMatcher, Method> symbolTable = new HashMap<MethodMatcher, Method>() {{
        put(new MethodMatcher("+", FloatType.INSTANCE), new Method(FloatType.INSTANCE, FloatType::plusFloat));
        put(new MethodMatcher("+", IntegerType.INSTANCE), new Method(FloatType.INSTANCE, FloatType::plusInteger));
        put(new MethodMatcher("*", FloatType.INSTANCE), new Method(FloatType.INSTANCE, FloatType::timesFloat));
        put(new MethodMatcher("*", IntegerType.INSTANCE), new Method(FloatType.INSTANCE, FloatType::timesInteger));
    }};

    private FloatType() {
        // nothing
    }

    @Override
    Map<MethodMatcher, Method> symbolTable() {
        return symbolTable;
    }

    @Override
    public int id() {
        return ID_FLOAT;
    }

    @Override
    public String toString() {
        return "Float";
    }

    private static Value plusFloat(Value left, Value right) {
        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static Value plusInteger(Value left, Value right) {
        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + ((Long) (right.value().internalValue())).doubleValue()
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static Value timesFloat(Value left, Value right) {
        if (isFloatZero(left) || isFloatZero(right)) {
            return DirectValue.build(0.0);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) * (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static boolean isFloatZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0.0);
    }

    private static Value timesInteger(Value left, Value right) {
        if (isFloatZero(left) || isIntegerZero(right)) {
            return DirectValue.build(0.0);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) * ((Long) (right.value().internalValue())).doubleValue()
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static boolean isIntegerZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0L);
    }
}
