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

package ch.pollet.thorium.evaluation;

import ch.pollet.thorium.utils.ListUtils;
import ch.pollet.thorium.values.types.Type;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class MethodMatcher {
    private String name;
    private List<Type> parameterTypes;

    public MethodMatcher(String name, Type... parameterTypes) {
        this.name = name;
        this.parameterTypes = Arrays.asList(parameterTypes);
    }

    @Override
    public String toString() {
        return name + "(" + ListUtils.concat(parameterTypes, ", ") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodMatcher)) return false;
        MethodMatcher that = (MethodMatcher) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes);
    }
}
