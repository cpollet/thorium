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

package net.cpollet.thorium.execution.data.method;

import net.cpollet.thorium.data.method.MethodBody;
import net.cpollet.thorium.data.method.MethodEvaluationContext;
import net.cpollet.thorium.data.method.MultivaluedOperator;
import net.cpollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class NativeMethodBody implements MethodBody {
    private final MultivaluedOperator<Value> methodBody;

    public NativeMethodBody(MultivaluedOperator<Value> methodBody) {
        this.methodBody = methodBody;
    }

    @Override
    public Value apply(MethodEvaluationContext evaluationContext) {
        return methodBody.apply(evaluationContext);
    }
}
