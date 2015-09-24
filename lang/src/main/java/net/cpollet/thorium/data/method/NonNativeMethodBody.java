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

import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.values.Value;

/**
 * @author Christophe Pollet
 */
public class NonNativeMethodBody implements MethodBody {
    private final ThoriumParser.StatementsContext statementsContext;

    public NonNativeMethodBody(ThoriumParser.StatementsContext statementsContext) {
        this.statementsContext = statementsContext;
    }

    @Override
    public Value apply(Value... values) {
        throw new IllegalStateException("not yet implemented");
    }
}
