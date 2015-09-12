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
        put(new MethodMatcher("<", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::lessThanFloat));
        put(new MethodMatcher("<", IntegerType.INSTANCE), new Method(IntegerType.INSTANCE, FloatType::lessThanInteger));
        put(new MethodMatcher("<=", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::lessThanOrEqualToFloat));
        put(new MethodMatcher("<=", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::lessThanOrEqualToInteger));
        put(new MethodMatcher(">", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::biggerThanFloat));
        put(new MethodMatcher(">", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::biggerThanInteger));
        put(new MethodMatcher(">=", FloatType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::biggerThanOrEqualToFloat));
        put(new MethodMatcher(">=", IntegerType.INSTANCE), new Method(BooleanType.INSTANCE, FloatType::biggerThanOrEqualToInteger));
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

    private static Value lessThanFloat(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanInteger(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToFloat(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToInteger(Value left, Value right) {
        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Type.BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value biggerThanFloat(Value right, Value left) {
        return BooleanType.not(lessThanOrEqualToFloat(right, left));
    }

    private static Value biggerThanInteger(Value left, Value right) {
        return BooleanType.not(lessThanOrEqualToInteger(left, right));
    }

    private static Value biggerThanOrEqualToFloat(Value left, Value right) {
        return BooleanType.not(lessThanFloat(left, right));
    }

    private static Value biggerThanOrEqualToInteger(Value left, Value right) {
        return BooleanType.not(lessThanInteger(left, right));
    }
}
