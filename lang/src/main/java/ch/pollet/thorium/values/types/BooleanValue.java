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

package ch.pollet.thorium.values.types;

import ch.pollet.thorium.values.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class BooleanValue extends BaseValue<BooleanValue> {
    private Boolean value;

    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);

    private static final Map<Boolean, BooleanValue> booleans = new HashMap<Boolean, BooleanValue>() {{
        put(true, BooleanValue.TRUE);
        put(false, BooleanValue.FALSE);
    }};

    public static BooleanValue build(boolean value) {
        return booleans.get(value);
    }

    private BooleanValue(Boolean value) {
        this.value = value;
    }

    Boolean getInternalValue() {
        return value;
    }

    @Override
    public Value operatorMultiply(Value other) {
        return operatorMultiply((BooleanValue) other);
    }

    @Override
    public Value operatorPlus(Value other) {
        return operatorPlus((BooleanValue) other);
    }

    public BooleanValue operatorPlus(BooleanValue right) {
        return build(value || right.getInternalValue());
    }

    public BooleanValue operatorMultiply(BooleanValue right) {
        return build(value && right.getInternalValue());
    }

    @Override
    public Type getType() {
        return BooleanType.INSTANCE;
    }

    @Override
    public String toString() {
        return "BooleanType{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanValue)) return false;
        BooleanValue that = (BooleanValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
