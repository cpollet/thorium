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

import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class NullValue extends BaseValue<NullValue> {
    // TODO rename
    public final static NullValue NULL = new NullValue();

    private NullValue() {
        // nothing
    }

    @Override
    public Type getType() {
        return NullType.INSTANCE;
    }

    @Override
    public Value operatorPlus(Value other) {
        return this;
    }

    @Override
    public Value operatorMultiply(Value other) {
        return this;
    }

    @Override
    public String toString() {
        return "NullValue";
    }
}
