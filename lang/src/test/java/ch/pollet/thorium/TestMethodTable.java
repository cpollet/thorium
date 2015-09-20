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

import ch.pollet.thorium.data.MethodSignature;
import ch.pollet.thorium.data.MethodTable;
import ch.pollet.thorium.execution.Operator;
import ch.pollet.thorium.types.Types;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class TestMethodTable {
    private MethodTable methodTable;
    private Operator[] operators = {
            values -> null, values -> null, values -> null, values -> null, values -> null,
            values -> null, values -> null, values -> null, values -> null, values -> null,
    };

    @Before
    public void setup() {
        methodTable = new MethodTable();

        // method Void.name() : Void
        methodTable.put("name", operators[0], Types.VOID, Types.VOID);

        // method Void.name(Void) : Void
        methodTable.put("name", operators[1], Types.VOID, Types.VOID, Types.VOID);

        // method Void.name(Void?) : Void
        methodTable.put("name", operators[2], Types.VOID, Types.VOID, Types.NULLABLE_VOID);

        // method Void.name(Void, Void) : Void
        methodTable.put("name", operators[3], Types.VOID, Types.VOID, Types.VOID, Types.VOID);

        // method Void.name(Void, Void?) : Void
        methodTable.put("name", operators[4], Types.VOID, Types.VOID, Types.VOID, Types.NULLABLE_VOID);

        // method Void.name(Integer?) : Void
        methodTable.put("name", operators[5], Types.VOID, Types.VOID, Types.NULLABLE_INTEGER);

        // method Void.name(Float, Float?) : Void
        methodTable.put("name", operators[6], Types.VOID, Types.VOID, Types.FLOAT, Types.NULLABLE_FLOAT);

        // method Void.name(Float?, Float) : Void
        methodTable.put("name", operators[7], Types.VOID, Types.VOID, Types.NULLABLE_FLOAT, Types.FLOAT);

        // method Void.name(Void, Float?, Float) : Void
        methodTable.put("name", operators[8], Types.VOID, Types.VOID, Types.VOID, Types.NULLABLE_FLOAT, Types.FLOAT);
    }

    @Test
    public void methodWithoutParameter() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID);

        assertThat(methodTable.get(method))
                .isSameAs(operators[0]);
    }

    @Test
    public void methodWithOneParameter_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void)
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID, Types.VOID);

        assertThat(methodTable.get(method))
                .isSameAs(operators[1]);
    }

    @Test
    public void methodWithThreeParameter_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void)
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID, Types.VOID, Types.FLOAT, Types.FLOAT);

        assertThat(methodTable.get(method))
                .isSameAs(operators[8]);
    }

    @Test
    public void methodWithTwoParameters_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void, Void)
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID, Types.VOID, Types.VOID);

        assertThat(methodTable.get(method))
                .isSameAs(operators[3]);
    }

    @Test
    public void methodWithTwoParameters_2() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void, Void?)
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID, Types.VOID, Types.NULLABLE_VOID);

        assertThat(methodTable.get(method))
                .isSameAs(operators[4]);
    }

    @Test
    public void methodWithOneParameter_3() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Integer)
        MethodSignature method = methodTable.lookupMethod("name", Types.VOID, Types.INTEGER);

        assertThat(methodTable.get(method))
                .isSameAs(operators[5]);
    }

    @Test
    public void methodNotFound() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        try {
            methodTable.lookupMethod("name", Types.VOID, Types.BOOLEAN);
        } catch (Exception e) {
            assertThat(e)
                    .hasMessage("Method not found.");
            return;
        }

        fail("Exception expected");
    }

    @Test
    public void duplicateMethodFound() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        try {
            methodTable.lookupMethod("name", Types.VOID, Types.FLOAT, Types.FLOAT);
        } catch (Exception e) {
            assertThat(e)
                    .hasMessage("Too many potential matches (2).");
            return;
        }

        fail("Exception expected");
    }

    public void fail(String message) {
        //noinspection ThrowableResultOfMethodCallIgnored see method's javadoc
        Fail.fail(message);
    }
}