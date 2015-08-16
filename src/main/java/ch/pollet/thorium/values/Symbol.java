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

import ch.pollet.thorium.values.types.Type;

/**
 * @author Christophe Pollet
 */
public abstract class Symbol implements Value {
    private String name;
    private Class<? extends Type> type;
    private Type value;

    protected Symbol(String name) {
        this.name = name;
    }

    public Symbol(String name, Value value) {
        this.name = name;
        this.type = value.getType();
        this.value = value.getValue();
    }

    public Symbol(String name, Class<? extends Type> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends Type> getType() {
        return type;
    }

    @Override
    public Type getValue() {
        return value;
    }
}
