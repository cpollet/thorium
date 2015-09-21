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
import ch.pollet.thorium.types.Types;
import ch.pollet.thorium.utils.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Christophe Pollet
 */
@Deprecated
public class MethodMatcher {
    private final String name;
    private final List<Type> parameterTypes;
    private final Type targetType;

    public MethodMatcher(Type targetType, String name, Type... parameterTypes) {
        this.targetType = targetType;
        this.name = name;
        this.parameterTypes = Arrays.asList(parameterTypes);
    }

    public MethodMatcher(String name, Type... parameterTypes) {
        this.targetType = Types.NULLABLE_VOID;
        this.name = name;
        this.parameterTypes = Arrays.asList(parameterTypes);
    }

    @Override
    public String toString() {
        return name + "(" + CollectionUtils.concat(parameterTypes, ", ") + ")";
    }

    @Override
    public boolean
    equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MethodMatcher)) {
            return false;
        }

        MethodMatcher that = (MethodMatcher) o;

        return Objects.equals(targetType, that.targetType) &&
                Objects.equals(name, that.name) &&
                Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType, name, parameterTypes);
    }
}
