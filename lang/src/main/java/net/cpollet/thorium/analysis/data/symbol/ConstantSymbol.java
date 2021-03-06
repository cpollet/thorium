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

package net.cpollet.thorium.analysis.data.symbol;

import net.cpollet.thorium.types.Type;
import org.antlr.v4.runtime.Token;

/**
 * @author Christophe Pollet
 */
public class ConstantSymbol extends Symbol {
    private boolean writable;

    public ConstantSymbol(String name, Token token, Type type) {
        super(name, token, type);
        this.writable = true;
    }

    @Override
    public void lock() {
        writable = false;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }
}
