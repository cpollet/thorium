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

package net.cpollet.thorium.values;

import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class DirectValue implements Value {
    public static final DirectValue VOID = new DirectValue();

    private static final Map<Object, DirectValue> valuesCache = new HashMap<Object, DirectValue>();

    static {
        valuesCache.put(Boolean.TRUE, new DirectValue(true));
        valuesCache.put(Boolean.FALSE, new DirectValue(false));
    }

    private Boolean booleanValue;
    private Double floatValue;
    private Long integerValue;

    private final Type type;

    private DirectValue() {
        this.type = Types.NULLABLE_VOID;
    }

    private DirectValue(Type type) {
        this.type = type;
    }

    private DirectValue(Boolean booleanValue) {
        this.type = Types.BOOLEAN;
        this.booleanValue = booleanValue;
    }

    private DirectValue(Long integerValue) {
        this.type = Types.INTEGER;
        this.integerValue = integerValue;
    }

    private DirectValue(Double floatValue) {
        this.type = Types.FLOAT;
        this.floatValue = floatValue;
    }

    public static DirectValue build(Type type) {
        if (!valuesCache.containsKey(type)) {
            valuesCache.put(type, new DirectValue(type));
        }

        return valuesCache.get(type);
    }

    @SuppressWarnings("SameReturnValue")
    public static DirectValue build() {
        return VOID;
    }

    public static DirectValue build(Boolean booleanValue) {
        return valuesCache.get(booleanValue);
    }

    public static DirectValue build(Long integerValue) {
        if (!valuesCache.containsKey(integerValue)) {
            valuesCache.put(integerValue, new DirectValue(integerValue));
        }

        return valuesCache.get(integerValue);
    }

    public static DirectValue build(Double doubleValue) {
        if (!valuesCache.containsKey(doubleValue)) {
            valuesCache.put(doubleValue, new DirectValue(doubleValue));
        }

        return valuesCache.get(doubleValue);
    }

    public Object internalValue() {
        switch (type.id()) {
            case BOOLEAN:
                return booleanValue;
            case INTEGER:
                return integerValue;
            case FLOAT:
                return floatValue;
            case VOID:
                return null;
            default:
                throw new IllegalStateException(type.id() + " is not a valid type id");
        }
    }

    @Override
    public boolean hasValue() {
        boolean notVoid = this != VOID;
        boolean booleanWithValue = type == Types.BOOLEAN || (type == Types.NULLABLE_BOOLEAN && booleanValue != null);
        boolean integerWithValue = type == Types.INTEGER || (type == Types.NULLABLE_INTEGER && integerValue != null);
        boolean floatWithValue = type == Types.FLOAT || (type == Types.NULLABLE_FLOAT && floatValue != null);

        return notVoid && (booleanWithValue || integerWithValue || floatWithValue);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public DirectValue value() {
        return this;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public String toString() {
        switch (type.id()) {
            case BOOLEAN:
                return type.toString() + "(" + booleanValue + ")";
            case INTEGER:
                return type.toString() + "(" + integerValue + ")";
            case FLOAT:
                return type.toString() + "(" + floatValue + ")";
            case VOID:
                return type.toString();
            default:
                throw new IllegalStateException(type.id() + " is not a valid type id");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DirectValue)) {
            return false;
        }

        DirectValue that = (DirectValue) o;

        return Objects.equals(booleanValue, that.booleanValue) &&
                Objects.equals(floatValue, that.floatValue) &&
                Objects.equals(integerValue, that.integerValue) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(booleanValue, floatValue, integerValue, type);
    }
}
