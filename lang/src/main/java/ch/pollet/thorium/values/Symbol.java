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

import ch.pollet.thorium.types.Type;

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
        this.type = Type.VOID;
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

    public void setType(Type type) {
        if (this.type != Type.VOID) {
            throw new IllegalStateException();
        }
        this.type = type;
    }

    public static Symbol create(SymbolType symbolType, String name) {
        switch (symbolType) {
            case VARIABLE:
                return new Variable(name);
            case CONSTANT:
                return new Constant(name);
        }

        throw new IllegalArgumentException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public boolean hasValue() {
        return value.hasValue();
    }

    @Override
    public DirectValue value() {
        return value;
    }

    @Override
    public String toString() {
        if (value.hasValue()) {
            return "Symbol(" + name + ": " + value + ")";
        }

        return "Symbol(" + name + ": " + type + ")";
    }
}
