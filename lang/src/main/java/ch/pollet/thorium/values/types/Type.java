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

import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.evaluation.Operator;

/**
 * @author Christophe Pollet
 */
public interface Type {
    int ID_VOID = 0;
    int ID_BOOLEAN = 1;
    int ID_INTEGER = 2;
    int ID_FLOAT = 3;

    Type BOOLEAN = BooleanType.INSTANCE;
    Type INTEGER = IntegerType.INSTANCE;
    Type FLOAT = FloatType.INSTANCE;
    Type VOID = VoidType.INSTANCE;

    int id();

    static boolean isAssignableFrom(Type target, Type source) {
        return target == null || target == VOID || target.equals(source);
    }

    Operator lookupMethod(MethodMatcher matcher);
}
