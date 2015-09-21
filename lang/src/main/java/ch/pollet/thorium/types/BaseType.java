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

import ch.pollet.thorium.data.Method;
import ch.pollet.thorium.data.MethodTable;
import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public abstract class BaseType implements Type {
    private Nullable nullable;

    public BaseType(Nullable nullable) {
        this.nullable = nullable;
    }

    @Override
    public boolean isNullable() {
        return nullable == Nullable.YES;
    }

    @Override
    public boolean isAssignableTo(Type target) {
        return id() == target.id() && (target.isNullable() || !isNullable());
    }

    @Override
    public Method lookupMethod(String name, Type... parametersType) {
        return methodTable().lookupMethod(name, this, parametersType);
    }

    abstract MethodTable methodTable();

    protected static Long integerValue(Value value) {
        return (Long) value.value().internalValue();
    }

    protected static Double floatValue(Value value) {
        return (Double) value.value().internalValue();
    }

    protected static Boolean booleanValue(Value value) {
        return (Boolean) value.value().internalValue();
    }

    @Override
    public String toString() {
        return nullable == Nullable.YES ? "?" : "";
    }
}
