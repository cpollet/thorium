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

package ch.pollet.thorium.execution.values;

import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.types.Types;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public abstract class Symbol implements Value {
    public enum SymbolType {
        VARIABLE, CONSTANT
    }

    private String name;
    private Type type;
    private DirectValue value;

    protected Symbol(String name) {
        this.name = name;
        this.type = Types.NULLABLE_VOID;
        this.value = DirectValue.build();
    }

    public Symbol(String name, DirectValue value) {
        this.name = name;
        this.type = value.type();
        this.value = value;
    }

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = DirectValue.build();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setType(Type type) {
        if (hasType() && this.type != type) {
            throw new IllegalStateException("Cannot change symbol " + name + " from type " + this.type() + " to " + type);
        }

        this.type = type;
    }

    @Override
    public Type type() {
        return type;
    }

    private boolean hasType() {
        return type != Types.NULLABLE_VOID;
    }

    public void setValue(DirectValue value) {
        if (!value.hasValue()) {
            throw new IllegalStateException("Cannot change " + name + "'s value to " + value + " which has no value");
        }
        this.value = value;
    }

    @Override
    public DirectValue value() {
        return value;
    }

    @Override
    public boolean hasValue() {
        return value.hasValue();
    }

    @Override
    public String toString() {
        if (value.hasValue()) {
            return "Symbol(" + name + ": " + value + ")";
        }

        return "Symbol(" + name + ": " + type + ")";
    }
}
