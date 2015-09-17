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
public class IntegerType extends BaseType {
    static final IntegerType INSTANCE = new IntegerType();

    private static final Map<MethodMatcher, Method> symbolTable = new HashMap<MethodMatcher, Method>() {{
        put(new MethodMatcher("+", FloatType.INSTANCE), new Method(FloatType.INSTANCE, IntegerType::plusFloat));
        put(new MethodMatcher("+", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, IntegerType::plusInteger));
        put(new MethodMatcher("*", FloatType.INSTANCE), new Method(FloatType.INSTANCE, IntegerType::timesFloat));
        put(new MethodMatcher("*", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, IntegerType::timesInteger));
        put(new MethodMatcher("<", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::lessThanInteger));
        put(new MethodMatcher("<", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::lessThanFloat));
        put(new MethodMatcher("<=", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::lessThanOrEqualToInteger));
        put(new MethodMatcher("<=", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::lessThanOrEqualToFloat));
        put(new MethodMatcher(">", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::biggerThanInteger));
        put(new MethodMatcher(">", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::biggerThanFloat));
        put(new MethodMatcher(">=", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::biggerThanOrEqualToInteger));
        put(new MethodMatcher(">=", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, IntegerType::biggerThanOrEqualToFloat));
    }};

    private IntegerType() {
        // nothing
    }

    @Override
    Map<MethodMatcher, Method> symbolTable() {
        return symbolTable;
    }

    @Override
    public int id() {
        return ID_INTEGER;
    }

    @Override
    public String toString() {
        return "Integer";
    }

    private static Value plusFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    ((Long) (left.value().internalValue())).doubleValue() + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static Value plusInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Long) (left.value().internalValue()) +
                            (Long) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.INTEGER);
    }

    private static Value timesFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (isIntegerZero(left) || isFloatZero(right)) {
            return DirectValue.build(0.0);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    ((Long) (left.value().internalValue())).doubleValue() * (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static boolean isFloatZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0.0);
    }

    private static Value timesInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (isIntegerZero(left) || isIntegerZero(right)) {
            return DirectValue.build(0L);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Long) (left.value().internalValue()) * (Long) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.INTEGER);
    }

    private static boolean isIntegerZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0L);
    }

    private static Value lessThanInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Long.compare(integerValue(left), integerValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(integerValue(left).doubleValue(), floatValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Long.compare(integerValue(left), integerValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(integerValue(left).doubleValue(), floatValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value biggerThanInteger(Value... values) {
        return BooleanType.not(lessThanOrEqualToInteger(values));
    }

    private static Value biggerThanFloat(Value... values) {
        return BooleanType.not(lessThanOrEqualToFloat(values));
    }


    private static Value biggerThanOrEqualToInteger(Value... values) {
        return BooleanType.not(lessThanInteger(values));
    }

    private static Value biggerThanOrEqualToFloat(Value... values) {
        return BooleanType.not(lessThanFloat(values));
    }
}
