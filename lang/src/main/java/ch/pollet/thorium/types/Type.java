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
import ch.pollet.thorium.data.MethodSignature;
import ch.pollet.thorium.execution.Method;
import ch.pollet.thorium.execution.MethodMatcher;

/**
 * @author Christophe Pollet
 */
public interface Type {
    int ID_VOID = 0;
    int ID_BOOLEAN = 1;
    int ID_INTEGER = 2;
    int ID_FLOAT = 3;

    enum Nullable {
        YES(true), NO(false), ANY(true);

        private final boolean nullable;

        Nullable(boolean nullable) {
            this.nullable = nullable;
        }

        public boolean isNullable() {
            return nullable;
        }

        public static Nullable get(boolean nullable) {
            return nullable ? YES : NO;
        }
    }

    @Deprecated
    static boolean isAssignableTo(Type destination, Type source) {
        if (destination == source) {
            return true;
        }

        if (destination == null || destination == Types.NULLABLE_VOID) {
            return true;
        }

        if (destination == Types.VOID && !source.isNullable()) {
            return true;
        }

        return source.isAssignableTo(destination);
    }

    int id();

    boolean isNullable();

    boolean isAssignableTo(Type target);

    Method lookupMethod(MethodMatcher matcher);

    Method2 lookupMethod(String name, Type... parametersType);

    Type nullable();

    Type nonNullable();
}
