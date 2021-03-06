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

package net.cpollet.thorium.types;

import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.data.method.MethodNotFoundException;
import net.cpollet.thorium.data.method.MethodTable;

import java.util.Collections;
import java.util.List;

/**
 * @author Christophe Pollet
 */
public class VoidType extends BaseType {
    static final VoidType NULLABLE = new VoidType(Nullable.YES);
    static final VoidType NON_NULLABLE = new VoidType(Nullable.NO); // TODO remove? void is by definition nullable

    private static final MethodTable methodTable = new MethodTable();

    private VoidType(Nullable nullable) {
        super(nullable);
    }

    @Override
    public Id id() {
        return Id.VOID;
    }

    @Override
    public Type nullable() {
        return NULLABLE;
    }

    @Override
    public Type nonNullable() {
        return NON_NULLABLE;
    }

    @Override
    public Method lookupMethod(String name, List<Type> parametersType) {
        throw new MethodNotFoundException("Method not found.", Collections.emptyList());
    }

    @Override
    MethodTable methodTable() {
        return methodTable;
    }

    @Override
    public String toString() {
        return "Void" + super.toString();
    }
}
