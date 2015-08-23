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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public interface Type {
    Map<Class<? extends Value>, String> types = new HashMap<Class<? extends Value>, String>() {{
        put(FloatValue.class, "Float");
        put(IntegerValue.class, "Integer");
        put(BooleanValue.class, "Boolean");
    }};

    static String getName(Class<? extends Type> typeClass) {
        return types.get(typeClass);
    }

    static boolean isAssignableFrom(Type target, Type source) {
        return target == null || target.equals(source);
    }
}
