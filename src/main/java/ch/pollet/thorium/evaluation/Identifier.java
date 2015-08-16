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

import ch.pollet.thorium.types.Type;

/**
 * @author Christophe Pollet
 */
public class Identifier implements Value {
    private String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<? extends Type> getType(EvaluationContext ctx) {
        return ctx.lookupSymbol(getName()).getType();
    }

    @Override
    public Type getValue(EvaluationContext ctx) {
        return ctx.lookupSymbol(name).getValue();
    }
}
