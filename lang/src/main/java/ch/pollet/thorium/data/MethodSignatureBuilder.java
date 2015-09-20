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

package ch.pollet.thorium.data;

import ch.pollet.thorium.types.Type;

import java.util.Arrays;
import java.util.List;

public class MethodSignatureBuilder {
    private final String name;
    private Type targetType;
    private Type returnType;
    private List<Type> parameterTypes;

    public static MethodSignatureBuilder method(String name) {
        return new MethodSignatureBuilder(name);
    }

    private MethodSignatureBuilder(String name){
        this.name = name;
    }

    public MethodSignatureBuilder withTargetType(Type targetType) {
        this.targetType = targetType;
        return this;
    }

    public MethodSignatureBuilder withReturnType(Type returnType) {
        this.returnType = returnType;
        return this;
    }

    public MethodSignatureBuilder withParameterTypes(Type... parameterTypes) {
        this.parameterTypes = Arrays.asList(parameterTypes);
        return this;
    }

    public MethodSignature build() {
        return new MethodSignature(name, targetType, returnType, parameterTypes);
    }
}