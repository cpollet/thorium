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
public class IntegerType extends BaseType {
    static final IntegerType INSTANCE = new IntegerType();

    private static final Map<MethodMatcher, Method> symbolTable = new HashMap<MethodMatcher, Method>() {{
        put(new MethodMatcher("+", FloatType.INSTANCE), new Method(FloatType.INSTANCE, IntegerType::plusFloat));
        put(new MethodMatcher("+", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, IntegerType::plusInteger));
        put(new MethodMatcher("*", FloatType.INSTANCE), new Method(FloatType.INSTANCE, IntegerType::timesFloat));
        put(new MethodMatcher("*", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, IntegerType::timesInteger));
        put(new MethodMatcher("<", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, IntegerType::lessThanInteger));
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

    private static Value plusFloat(Value left, Value right) {
        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    ((Long) (left.value().internalValue())).doubleValue() + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.FLOAT);
    }

    private static Value plusInteger(Value left, Value right) {
        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Long) (left.value().internalValue()) +
                            (Long) (right.value().internalValue())
            );
        }

        return DirectValue.build(Type.INTEGER);
    }

    private static Value timesFloat(Value left, Value right) {
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

    private static Value timesInteger(Value left, Value right) {
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

    private static Value lessThanInteger(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Long.compare(integerValue(left), integerValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanFloat(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(integerValue(left).doubleValue(), floatValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToInteger(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Long.compare(integerValue(left), integerValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToFloat(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(integerValue(left).doubleValue(), floatValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value biggerThanInteger(Value left, Value right) {
        return BooleanType.not(lessThanOrEqualToInteger(left, right));
    }

    private static Value biggerThanFloat(Value left, Value right) {
        return BooleanType.not(lessThanOrEqualToFloat(left, right));
    }


    private static Value biggerThanOrEqualToInteger(Value left, Value right) {
        return BooleanType.not(lessThanInteger(left, right));
    }

    private static Value biggerThanOrEqualToFloat(Value left, Value right) {
        return BooleanType.not(lessThanFloat(left, right));
    }
}
