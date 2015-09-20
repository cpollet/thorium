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

package ch.pollet.thorium;

import ch.pollet.thorium.analysis.MethodSignature;
import ch.pollet.thorium.execution.Operator;
import ch.pollet.thorium.types.Types;
import ch.pollet.thorium.values.Value;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class TestMethodTable {
    private MethodTable methodTable;
    private Operator operator = values -> null;

    @Before
    public void setup() {
        methodTable = new MethodTable();
    }

    @Test
    public void methodWithoutParameter() {
        // GIVEN
        // method Void.name() : Void
        methodTable.put(operator, "name", Types.VOID, Types.VOID);

        // WHEN
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID);

        assertThat(methodTable.get(method))
                .isSameAs(operator);
    }


}
