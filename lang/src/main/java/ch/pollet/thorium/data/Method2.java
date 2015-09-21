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

import ch.pollet.thorium.execution.Operator;

/**
 * @author Christophe Pollet
 */
// TODO rename to Method
public class Method2 {
    private final MethodSignature methodSignature;
    private final Operator operator;

    public Method2(MethodSignature methodSignature, Operator operator) {
        this.methodSignature = methodSignature;
        this.operator = operator;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    // FIXME rename
    public Operator getOperator() {
        return operator;
    }
}
