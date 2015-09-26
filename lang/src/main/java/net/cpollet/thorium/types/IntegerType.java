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

package net.cpollet.thorium.types;

import net.cpollet.thorium.data.method.MethodTable;
import net.cpollet.thorium.execution.ExecutionContext;
import net.cpollet.thorium.values.DirectValue;
import net.cpollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class IntegerType extends BaseType {
    static final IntegerType NULLABLE = new IntegerType(Nullable.YES);
    static final IntegerType NON_NULLABLE = new IntegerType(Nullable.NO);

    private static final MethodTable methodTable = new MethodTable();

    static {
        methodTable.put("+", body(IntegerType::plusInteger), IntegerType.NULLABLE, IntegerType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("+", body(IntegerType::plusInteger), IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("*", body(IntegerType::timesInteger), IntegerType.NULLABLE, IntegerType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("*", body(IntegerType::timesInteger), IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("+", body(IntegerType::plusFloat), IntegerType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("+", body(IntegerType::plusFloat), IntegerType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("*", body(IntegerType::timesFloat), IntegerType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("*", body(IntegerType::timesFloat), IntegerType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("<", body(IntegerType::lessThanInteger), IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<", body(IntegerType::lessThanInteger), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put("<=", body(IntegerType::lessThanOrEqualToInteger), IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<=", body(IntegerType::lessThanOrEqualToInteger), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">", body(IntegerType::biggerThanInteger), IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">", body(IntegerType::biggerThanInteger), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">=", body(IntegerType::biggerThanOrEqualToInteger), IntegerType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">=", body(IntegerType::biggerThanOrEqualToInteger), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("<", body(IntegerType::lessThanFloat), IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<", body(IntegerType::lessThanFloat), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put("<=", body(IntegerType::lessThanOrEqualToFloat), IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<=", body(IntegerType::lessThanOrEqualToFloat), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">", body(IntegerType::biggerThanFloat), IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">", body(IntegerType::biggerThanFloat), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">=", body(IntegerType::biggerThanOrEqualToFloat), IntegerType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">=", body(IntegerType::biggerThanOrEqualToFloat), IntegerType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
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

    private static Value plusFloat(ExecutionContext executionContext, Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    ((Long) (left.value().internalValue())).doubleValue() + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static Value plusInteger(ExecutionContext executionContext, Value... values) {
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

    private static Value timesFloat(ExecutionContext executionContext, Value... values) {
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

    private static Value timesInteger(ExecutionContext executionContext, Value... values) {
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

    private static Value lessThanInteger(ExecutionContext executionContext, Value... values) {
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

    private static Value lessThanFloat(ExecutionContext executionContext, Value... values) {
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

    private static Value lessThanOrEqualToInteger(ExecutionContext executionContext, Value... values) {
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

    private static Value lessThanOrEqualToFloat(ExecutionContext executionContext, Value... values) {
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

    private static Value biggerThanInteger(ExecutionContext executionContext, Value... values) {
        return BooleanType.not(executionContext, lessThanOrEqualToInteger(executionContext, values));
    }

    private static Value biggerThanFloat(ExecutionContext executionContext, Value... values) {
        return BooleanType.not(executionContext, lessThanOrEqualToFloat(executionContext, values));
    }


    private static Value biggerThanOrEqualToInteger(ExecutionContext executionContext, Value... values) {
        return BooleanType.not(executionContext, lessThanInteger(executionContext, values));
    }

    private static Value biggerThanOrEqualToFloat(ExecutionContext executionContext, Value... values) {
        return BooleanType.not(executionContext, lessThanFloat(executionContext, values));
    }
}
