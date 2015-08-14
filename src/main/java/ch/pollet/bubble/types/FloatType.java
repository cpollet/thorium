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

package ch.pollet.bubble.types;

import ch.pollet.bubble.evaluation.EvaluationContext;

import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class FloatType extends BaseType {
    public Double value;

    public FloatType(Double value) {
        this.value = value;
    }

    Double getInternalValue() {
        return value;
    }

    public FloatType operatorPlus(FloatType right) {
        return new FloatType(value + right.getInternalValue());
    }

    public FloatType operatorPlus(IntegerType right) {
        return new FloatType(value + right.getInternalValue());
    }

    public FloatType operatorMultiply(FloatType right) {
        return new FloatType(value * right.getInternalValue());
    }

    public FloatType operatorMultiply(IntegerType right) {
        return new FloatType(value * right.getInternalValue());
    }

    @Override
    public Class<? extends Type> getType(EvaluationContext ctx) {
        return getClass();
    }

    @Override
    public String toString() {
        return "FloatType{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FloatType)) return false;
        FloatType floatType = (FloatType) o;
        return Objects.equals(value, floatType.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
