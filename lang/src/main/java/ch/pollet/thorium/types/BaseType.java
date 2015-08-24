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

import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.evaluation.Operator;
import ch.pollet.thorium.semantic.exception.MethodNotFoundException;

import java.util.Map;

/**
 * @author Christophe Pollet
 */
public abstract class BaseType implements Type {
    @Override
    public Operator lookupMethod(MethodMatcher matcher) {
        if (symbolTable().containsKey(matcher)) {
            return symbolTable().get(matcher);
        }

        throw new MethodNotFoundException("Method " + matcher + " not implemented on " + this);
    }

    abstract Map<MethodMatcher, Operator> symbolTable();
}