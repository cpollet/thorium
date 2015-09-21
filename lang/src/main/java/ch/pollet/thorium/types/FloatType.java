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

import ch.pollet.thorium.data.MethodTable;
import ch.pollet.thorium.execution.Method;
import ch.pollet.thorium.execution.MethodMatcher;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class FloatType extends BaseType {
    static final FloatType NULLABLE = new FloatType(Nullable.YES);
    static final FloatType NON_NULLABLE = new FloatType(Nullable.NO);

    private static final Map<MethodMatcher, Method> symbolTable = new HashMap<>();

    private static final MethodTable methodTable = new MethodTable();

    static {
        methodTable.put("+", FloatType::plusFloat, FloatType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("+", FloatType::plusFloat, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("*", FloatType::timesFloat, FloatType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("*", FloatType::timesFloat, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("+", FloatType::plusInteger, FloatType.NULLABLE, FloatType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("+", FloatType::plusInteger, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("*", FloatType::timesInteger, FloatType.NULLABLE, FloatType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("*", FloatType::timesInteger, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("<", FloatType::lessThanFloat, FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<", FloatType::lessThanFloat, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put("<=", FloatType::lessThanOrEqualToFloat, FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<=", FloatType::lessThanOrEqualToFloat, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">", FloatType::biggerThanFloat, FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">", FloatType::biggerThanFloat, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">=", FloatType::biggerThanOrEqualToFloat, FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">=", FloatType::biggerThanOrEqualToFloat, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("<", FloatType::lessThanInteger, FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<", FloatType::lessThanInteger, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put("<=", FloatType::lessThanOrEqualToInteger, FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<=", FloatType::lessThanOrEqualToInteger, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">", FloatType::biggerThanInteger, FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">", FloatType::biggerThanInteger, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">=", FloatType::biggerThanOrEqualToInteger, FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">=", FloatType::biggerThanOrEqualToInteger, FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
    }

    private FloatType(Nullable nullable) {
        super(nullable);
    }

    @Override
    Map<MethodMatcher, Method> symbolTable() {
        return symbolTable;
    }

    @Override
    public MethodTable methodTable() {
        return methodTable;
    }

    @Override
    public int id() {
        return ID_FLOAT;
    }

    @Override
    public Type nullable() {
        return NULLABLE;
    }

    @Override
    public Type nonNullable() {
        return NON_NULLABLE;
    }

    @Override
    public String toString() {
        return "Float" + super.toString();
    }

    private static Value plusFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static Value plusInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + ((Long) (right.value().internalValue())).doubleValue()
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static Value timesFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (isFloatZero(left) || isFloatZero(right)) {
            return DirectValue.build(0.0);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) * (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static boolean isFloatZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0.0);
    }

    private static Value timesInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (isFloatZero(left) || isIntegerZero(right)) {
            return DirectValue.build(0.0);
        }

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) * ((Long) (right.value().internalValue())).doubleValue()
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static boolean isIntegerZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0L);
    }

    private static Value lessThanFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value biggerThanFloat(Value... values) {
        return BooleanType.not(lessThanOrEqualToFloat(values));
    }

    private static Value biggerThanInteger(Value... values) {
        return BooleanType.not(lessThanOrEqualToInteger(values));
    }

    private static Value biggerThanOrEqualToFloat(Value... values) {
        return BooleanType.not(lessThanFloat(values));
    }

    private static Value biggerThanOrEqualToInteger(Value... values) {
        return BooleanType.not(lessThanInteger(values));
    }
}
