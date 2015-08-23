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

package ch.pollet.thorium.values;

import ch.pollet.thorium.values.types.NullValue;
import ch.pollet.thorium.values.types.Type;

/**
 * @author Christophe Pollet
 */
public abstract class Symbol implements Value {
    private String name;
    private Type type;
    private Value value;

    protected Symbol(String name) {
        this.name = name;
        // this.type = NullType.INSTANCE;
        this.value = NullValue.NULL;
    }

    public Symbol(String name, Value<? extends Type> value) {
        this.name = name;
        this.type = value.getType();
        this.value = value;
    }

    public Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
        this.value = NullValue.NULL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    // @Override
    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "name='" + name + '\'' +
                ", type=" + (type == null ? "?" : type.toString()) +
                ", value=" + value +
                '}';
    }
}
