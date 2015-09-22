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

package net.cpollet.thorium.execution.values;

import net.cpollet.thorium.values.DirectValue;

/**
 * @author Christophe Pollet
 */
// TODO should be deleted and replaced by Variable instead, since semantic checks are already done...
public class Constant extends Variable {

    public Constant(String name) {
        super(name);
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
