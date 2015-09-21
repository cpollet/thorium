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

package ch.pollet.thorium.execution;

import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
@Deprecated
public class Method {
    private final Type type;
    private final Operator op;

    public Method(Type type, Operator op) {
        this.type = type;
        this.op = op;
    }

    public Type getType() {
        return type;
    }

    public Value apply(Value... values) {
        return op.apply(values);
    }
}
