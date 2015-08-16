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
public class Variable extends Symbol {
    private String name;

    public Variable(String name, Class<? extends Type> type) {
        super(name, type);
        this.name = name;
    }

    public Variable(String name, Value value) {
        super(name, value.getValue());
        this.name = name;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }
}
