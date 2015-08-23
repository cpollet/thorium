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

import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;

import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class IntegerValue extends BaseValue<IntegerValue> {
    private Long value;

    public IntegerValue(Long value) {
        this.value = value;
    }

    Long getInternalValue() {
        return value;
    }

    @Override
    public Value operatorMultiply(Value other) {
        if (other instanceof Symbol) {
            other = ((Symbol) other).getValue();
        }
        if (other instanceof IntegerValue) {
            return operatorMultiply((IntegerValue) other);
        } else {
            return operatorMultiply((FloatValue) other);
        }
    }

    @Override
    public Value operatorPlus(Value other) {
        if (other instanceof Symbol) {
            other = ((Symbol) other).getValue();
        }
        if (other instanceof IntegerValue) {
            return operatorPlus((IntegerValue) other);
        } else {
            return operatorPlus((FloatValue) other);
        }
    }

    public IntegerValue operatorPlus(IntegerValue right) {
        return new IntegerValue(value + right.getInternalValue());
    }

    public FloatValue operatorPlus(FloatValue right) {
        return new FloatValue(value + right.getInternalValue());
    }

    public IntegerValue operatorMultiply(IntegerValue right) {
        return new IntegerValue(value * right.getInternalValue());
    }

    public FloatValue operatorMultiply(FloatValue right) {
        return new FloatValue(value * right.getInternalValue());
    }

    @Override
    public Type getType() {
        return IntegerType.INSTANCE;
    }

    @Override
    public String toString() {
        return "IntegerType{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntegerValue)) return false;
        IntegerValue that = (IntegerValue) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
