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

package net.pollet.thorium.values;

import net.pollet.thorium.execution.values.Symbol;
import net.pollet.thorium.types.Type;
import net.pollet.thorium.types.Types;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestSymbol {
    @Test
    public void toStringWithOnlyName() {
        // GIVEN
        Symbol symbol = new SymbolForTests("name");

        // WHEN
        String string = symbol.toString();

        // THEN
        assertThat(string).isEqualTo("Symbol(name: Void?)");
    }

    @Test
    public void toStringWithType() {
        // GIVEN
        Symbol symbol = new SymbolForTests("name", Types.NULLABLE_INTEGER);

        // WHEN
        String string = symbol.toString();

        // THEN
        assertThat(string).isEqualTo("Symbol(name: Integer?)");
    }

    @Test
    public void toStringWithValue() {
        // GIVEN
        Symbol symbol = new SymbolForTests("name", DirectValue.build(1L));

        // WHEN
        String string = symbol.toString();

        // THEN
        assertThat(string).isEqualTo("Symbol(name: Integer(1))");
    }

    private class SymbolForTests extends Symbol {
        public SymbolForTests(String name) {
            super(name);
        }

        public SymbolForTests(String name, DirectValue value) {
            super(name, value);
        }

        public SymbolForTests(String name, Type type) {
            super(name, type);
        }

        @Override
        public boolean isWritable() {
            return false;
        }
    }
}
