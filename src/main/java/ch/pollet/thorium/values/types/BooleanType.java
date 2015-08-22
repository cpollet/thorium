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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class BooleanType extends BaseType<BooleanType> {
    private Boolean value;

    public static final BooleanType TRUE = new BooleanType(true);
    public static final BooleanType FALSE = new BooleanType(false);

    private static final Map<Boolean, BooleanType> booleans = new HashMap<Boolean, BooleanType>() {{
        put(true, BooleanType.TRUE);
        put(false, BooleanType.FALSE);
    }};

    public static BooleanType build(boolean value) {
        return booleans.get(value);
    }

    private BooleanType(Boolean value) {
        this.value = value;
    }

    Boolean getInternalValue() {
        return value;
    }

    public BooleanType operatorPlus(BooleanType right) {
        return build(value || right.getInternalValue());
    }

    public BooleanType operatorMultiply(BooleanType right) {
        return build(value && right.getInternalValue());
    }

    @Override
    public Class<? extends Type> getType() {
        return getClass();
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
        if (!(o instanceof BooleanType)) return false;
        BooleanType that = (BooleanType) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
