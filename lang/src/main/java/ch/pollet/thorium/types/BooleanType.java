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
public class BooleanType extends BaseType {
    static final BooleanType NULLABLE = new BooleanType(Nullable.YES);
    static final BooleanType NON_NULLABLE = new BooleanType(Nullable.NO);

    private static final MethodTable methodTable = new MethodTable();

    static {
        methodTable.put("+", BooleanType::or, BooleanType.NULLABLE, BooleanType.NULLABLE, BooleanType.NULLABLE);
        methodTable.put("+", BooleanType::or, BooleanType.NON_NULLABLE, BooleanType.NON_NULLABLE, BooleanType.NON_NULLABLE);

        methodTable.put("*", BooleanType::and, BooleanType.NULLABLE, BooleanType.NULLABLE, BooleanType.NULLABLE);
        methodTable.put("*", BooleanType::and, BooleanType.NON_NULLABLE, BooleanType.NON_NULLABLE, BooleanType.NON_NULLABLE);

        methodTable.put("!", BooleanType::not, BooleanType.NULLABLE, BooleanType.NULLABLE);
        methodTable.put("!", BooleanType::not, BooleanType.NON_NULLABLE, BooleanType.NON_NULLABLE);
    }

    private BooleanType(Nullable nullable) {
        super(nullable);
    }

    @Override
    public MethodTable methodTable() {
        return methodTable;
    }

    @Override
    public Id id() {
        return Id.BOOLEAN;
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
        return "Boolean" + super.toString();
    }

    private static Value or(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Boolean) (left.value().internalValue()) || (Boolean) (right.value().internalValue())
            );
        }

        if (evaluatesToTrue(left)) {
            return left;
        }
        if (evaluatesToTrue(right)) {
            return right;
        }

        return DirectValue.build(Types.NULLABLE_BOOLEAN);
    }

    private static boolean evaluatesToTrue(Value value) {
        return value.hasValue() && (Boolean) (value.value().internalValue());
    }

    private static Value and(Value... values) {
        Value left = values[0];
        Value right = values[1];

        if (left.hasValue() && right.hasValue()) {
            return DirectValue.build(
                    (Boolean) (left.value().internalValue()) && (Boolean) (right.value().internalValue())
            );
        }

        if (evaluatesToFalse(left)) {
            return left;
        }
        if (evaluatesToFalse(right)) {
            return right;
        }

        return DirectValue.build(Types.NULLABLE_BOOLEAN);
    }

    private static boolean evaluatesToFalse(Value value) {
        return value.hasValue() && !(Boolean) (value.value().internalValue());
    }

    public static Value not(Value... values) {
        Value value = values[0];

        if (!value.hasValue()) {
            return DirectValue.build(Types.NULLABLE_BOOLEAN);
        }

        return DirectValue.build(!booleanValue(value));
    }
}
