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

package net.pollet.thorium.data.method;

/**
 * @author Christophe Pollet
 */
public class Method {
    private final MethodSignature methodSignature;
    private final MethodBody methodBody;

    public Method(MethodSignature methodSignature, MethodBody methodBody) {
        this.methodSignature = methodSignature;
        this.methodBody = methodBody;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    public MethodBody getMethodBody() {
        return methodBody;
    }
}
