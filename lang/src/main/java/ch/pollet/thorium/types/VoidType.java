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

import ch.pollet.thorium.data.Method2;
import ch.pollet.thorium.data.MethodNotFoundException;
import ch.pollet.thorium.execution.Method;
import ch.pollet.thorium.execution.MethodMatcher;

import java.util.Collections;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class VoidType extends BaseType {
    static final VoidType NULLABLE = new VoidType(Nullable.YES);
    static final VoidType NON_NULLABLE = new VoidType(Nullable.NO); // FIXME remove, void is by definition nullable

    private VoidType(Nullable nullable) {
        super(nullable);
    }

    @Override
    public int id() {
        return ID_VOID;
    }

    @Override
    public Type nullable() {
        return NULLABLE;
    }

    @Override
    public Type nonNullable() {
        return NON_NULLABLE;
    }

    @Deprecated
    @Override
    public Method lookupMethod(MethodMatcher matcher) {
        return null;
    }

    @Override
    public Method2 lookupMethod(String name, Type... parametersType) {
        throw new MethodNotFoundException("Method not found.", Collections.emptyList());
    }

    @Override
    Map<MethodMatcher, Method> symbolTable() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return "Void" + super.toString();
    }
}
