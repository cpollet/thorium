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

import net.cpollet.thorium.data.method.MethodEvaluationContext;
import net.cpollet.thorium.data.method.MethodTable;
import net.cpollet.thorium.values.DirectValue;
import net.cpollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class FloatType extends BaseType {
    static final FloatType NULLABLE = new FloatType(Nullable.YES);
    static final FloatType NON_NULLABLE = new FloatType(Nullable.NO);

    private static final MethodTable methodTable = new MethodTable();

    static {
        methodTable.put("+", body(FloatType::plusFloat), FloatType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("+", body(FloatType::plusFloat), FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("*", body(FloatType::timesFloat), FloatType.NULLABLE, FloatType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("*", body(FloatType::timesFloat), FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("+", body(FloatType::plusInteger), FloatType.NULLABLE, FloatType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("+", body(FloatType::plusInteger), FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("*", body(FloatType::timesInteger), FloatType.NULLABLE, FloatType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("*", body(FloatType::timesInteger), FloatType.NON_NULLABLE, FloatType.NON_NULLABLE, IntegerType.NON_NULLABLE);

        methodTable.put("<", body(FloatType::lessThanFloat), FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<", body(FloatType::lessThanFloat), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put("<=", body(FloatType::lessThanOrEqualToFloat), FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put("<=", body(FloatType::lessThanOrEqualToFloat), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">", body(FloatType::biggerThanFloat), FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">", body(FloatType::biggerThanFloat), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);
        methodTable.put(">=", body(FloatType::biggerThanOrEqualToFloat), FloatType.NULLABLE, BooleanType.NULLABLE, FloatType.NULLABLE);
        methodTable.put(">=", body(FloatType::biggerThanOrEqualToFloat), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, FloatType.NON_NULLABLE);

        methodTable.put("<", body(FloatType::lessThanInteger), FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<", body(FloatType::lessThanInteger), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put("<=", body(FloatType::lessThanOrEqualToInteger), FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put("<=", body(FloatType::lessThanOrEqualToInteger), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">", body(FloatType::biggerThanInteger), FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">", body(FloatType::biggerThanInteger), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
        methodTable.put(">=", body(FloatType::biggerThanOrEqualToInteger), FloatType.NULLABLE, BooleanType.NULLABLE, IntegerType.NULLABLE);
        methodTable.put(">=", body(FloatType::biggerThanOrEqualToInteger), FloatType.NON_NULLABLE, BooleanType.NON_NULLABLE, IntegerType.NON_NULLABLE);
    }

    private FloatType(Nullable nullable) {
        super(nullable);
    }

    @Override
    public MethodTable methodTable() {
        return methodTable;
    }

    @Override
    public Id id() {
        return Id.FLOAT;
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

    private static Value plusFloat(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + (Double) (right.value().internalValue())
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static Value plusInteger(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Double) (left.value().internalValue()) + ((Long) (right.value().internalValue())).doubleValue()
            );
        }

        return DirectValue.build(Types.NULLABLE_FLOAT);
    }

    private static Value timesFloat(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

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

    private static Value timesInteger(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

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

    private static Value lessThanFloat(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanInteger(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) < 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToFloat(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), floatValue(right)) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value lessThanOrEqualToInteger(MethodEvaluationContext evaluationContext) {
        Value left = evaluationContext.getParameter(0);
        Value right = evaluationContext.getParameter(1);

        if (!left.hasValue() || !right.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        if (Double.compare(floatValue(left), integerValue(right).doubleValue()) <= 0) {
            return DirectValue.build(true);
        }

        return DirectValue.build(false);
    }

    private static Value biggerThanFloat(MethodEvaluationContext evaluationContext) {
        return BooleanType.not(new MethodEvaluationContext(evaluationContext.getExecutionContext(), lessThanOrEqualToFloat(evaluationContext)));
    }

    private static Value biggerThanInteger(MethodEvaluationContext evaluationContext) {
        return BooleanType.not(new MethodEvaluationContext(evaluationContext.getExecutionContext(), lessThanOrEqualToInteger(evaluationContext)));
    }

    private static Value biggerThanOrEqualToFloat(MethodEvaluationContext evaluationContext) {
        return BooleanType.not(new MethodEvaluationContext(evaluationContext.getExecutionContext(), lessThanFloat(evaluationContext)));
    }

    private static Value biggerThanOrEqualToInteger(MethodEvaluationContext evaluationContext) {
        return BooleanType.not(new MethodEvaluationContext(evaluationContext.getExecutionContext(), lessThanInteger(evaluationContext)));
    }
}
