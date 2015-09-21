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

import ch.pollet.thorium.data.method.MethodTable;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class IntegerType extends BaseType {
    static final IntegerType NULLABLE = new IntegerType(Nullable.YES);
    static final IntegerType NON_NULLABLE = new IntegerType(Nullable.NO);

    private static final MethodTable methodTable = new MethodTable();

    static {
        methodTable.put("+", IntegerType::plusInteger, IntegerType.NULLABLE, IntegerType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("+", IntegerType::plusInteger, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("*", IntegerType::timesInteger, IntegerType.NULLABLE, IntegerType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("*", IntegerType::timesInteger, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("+", IntegerType::plusFloat, IntegerType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("+", IntegerType::plusFloat, IntegerType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("*", IntegerType::timesFloat, IntegerType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("*", IntegerType::timesFloat, IntegerType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("<", IntegerType::lessThanInteger, IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<", IntegerType::lessThanInteger, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put("<=", IntegerType::lessThanOrEqualToInteger, IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<=", IntegerType::lessThanOrEqualToInteger, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">", IntegerType::biggerThanInteger, IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">", IntegerType::biggerThanInteger, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">=", IntegerType::biggerThanOrEqualToInteger, IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">=", IntegerType::biggerThanOrEqualToInteger, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("<", IntegerType::lessThanFloat, IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<", IntegerType::lessThanFloat, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put("<=", IntegerType::lessThanOrEqualToFloat, IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<=", IntegerType::lessThanOrEqualToFloat, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">", IntegerType::biggerThanFloat, IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">", IntegerType::biggerThanFloat, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">=", IntegerType::biggerThanOrEqualToFloat, IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">=", IntegerType::biggerThanOrEqualToFloat, IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
    }

    private IntegerType(Nullable nullable) {
        super(nullable);
    }

    @Override
    public MethodTable methodTable() {
        return methodTable;
    }

    @Override
    public Id id() {
        return Id.INTEGER;
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
        return "Integer" + super.toString();
    }

    private static Value plusFloat(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    ((Long) (left.value().internalValue())).doubleValue() + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
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

        return DirectValue.build(Types.NULLABLE_INTEGER);
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

        return DirectValue.build(Types.NULLABLE_FLOAT);
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

        return DirectValue.build(Types.NULLABLE_INTEGER);
    }

    private static boolean isIntegerZero(Value value) {
        return value.hasValue() && value.value().internalValue().equals(0L);
    }

    private static Value lessThanInteger(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
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
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
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
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
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
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
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
