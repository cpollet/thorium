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

/**
 * @author Christophe Pollet
 */
public class Types {
    public static final Type NULLABLE_INTEGER = IntegerType.NULLABLE;
    public static final Type INTEGER = IntegerType.NON_NULLABLE;

    public static final Type NULLABLE_FLOAT = FloatType.NULLABLE;
    public static final Type FLOAT = FloatType.NON_NULLABLE;

    public static final Type NULLABLE_BOOLEAN = BooleanType.NULLABLE;
    public static final Type BOOLEAN = BooleanType.NON_NULLABLE;

    public static final Type NULLABLE_VOID = VoidType.NULLABLE;
    public static final Type VOID = VoidType.NON_NULLABLE;

    private Types() {
        // nothing
    }

    public static Type get(Type type, Type.Nullable nullable) {
        switch (nullable) {
            case YES:
                return type.nullable();
            case NO:
                return type.nonNullable();
            default:
                throw new IllegalArgumentException(nullable + " is not a valid argument. Expected: YES or NO");
        }
    }
}
