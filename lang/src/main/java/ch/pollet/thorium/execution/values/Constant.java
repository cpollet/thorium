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
import ch.pollet.thorium.values.DirectValue;

/**
 * @author Christophe Pollet
 */
public class Constant extends Variable {

    public Constant(String name) {
        super(name);
    }

    public Constant(String name, Type type) {
        super(name, type);
    }

    public Constant(String name, DirectValue value) {
        super(name, value);
    }

    @Override
    public void setValue(DirectValue value) {
        if (!isWritable()) {
            throw new IllegalStateException("Cannot change value of constant " + getName());
        }

        super.setValue(value);
    }

    @Override
    public boolean isWritable() {
        return !hasValue();
    }
}