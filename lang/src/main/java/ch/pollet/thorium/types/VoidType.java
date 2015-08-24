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

import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.evaluation.Operator;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class VoidType implements Type {
    static final VoidType INSTANCE = new VoidType();

    private VoidType() {
        // nothing
    }

    @Override
    public int id() {
        return ID_VOID;
    }

    @Override
    public Operator lookupMethod(MethodMatcher matcher) {
        return VoidType::noValue;
    }

    @Override
    public String toString() {
        return "Void";
    }

    private static Value noValue(Value left, Value right) {
        return DirectValue.build();
    }
}