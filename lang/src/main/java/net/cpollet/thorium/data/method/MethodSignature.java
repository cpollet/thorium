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

package net.cpollet.thorium.data.method;

import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.utils.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class MethodSignature {
    private final String name;
    private final Type targetType;
    private final Type returnType;
    private final List<ParameterSignature> parameterSignatures;

    public MethodSignature(String name, Type targetType, Type returnType, List<ParameterSignature> parameterSignatures) {
        this.name = name;
        this.targetType = targetType;
        this.returnType = returnType;
        this.parameterSignatures = parameterSignatures;
    }

    public String getName() {
        return name;
    }

    public Type getTargetType() {
        return targetType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Type> getParameterTypes() {
        return parameterSignatures.stream()
                .map(ParameterSignature::getType)
                .collect(Collectors.toList());
    }

    public String getParameterName(int index) {
        return parameterSignatures.get(index).getName();
    }

    @Override
    public String toString() {
        return name + "(" + CollectionUtils.concat(getParameterTypes(), ", ") + ") : " + returnType.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MethodSignature)) {
            return false;
        }

        MethodSignature that = (MethodSignature) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(targetType, that.targetType) &&
                Objects.equals(returnType, that.returnType) &&
                Objects.equals(getParameterTypes(), that.getParameterTypes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetType, returnType, getParameterTypes());
    }
}
