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

import net.cpollet.thorium.types.Types;
import net.cpollet.thorium.values.DirectValue;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class TestMethodTable {
    private MethodTable methodTable;
    private MethodBody[] methodBodies = {
            (context, values) -> DirectValue.build(0L),
            (context, values) -> DirectValue.build(1L),
            (context, values) -> DirectValue.build(2L),
            (context, values) -> DirectValue.build(3L),
            (context, values) -> DirectValue.build(4L),
            (context, values) -> DirectValue.build(5L),
            (context, values) -> DirectValue.build(6L),
            (context, values) -> DirectValue.build(7L),
            (context, values) -> DirectValue.build(8L),
            (context, values) -> DirectValue.build(9L),
    };

    @Before
    public void setup() {
        methodTable = new MethodTable();

        // method Void.name() : Void
        methodTable.put("name", methodBodies[0], Types.VOID, Types.VOID);

        // method Void.name(Void) : Void
        methodTable.put("name", methodBodies[1], Types.VOID, Types.VOID, Types.VOID);

        // method Void.name(Void?) : Void
        methodTable.put("name", methodBodies[2], Types.VOID, Types.VOID, Types.NULLABLE_VOID);

        // method Void.name(Void, Void) : Void
        methodTable.put("name", methodBodies[3], Types.VOID, Types.VOID, Types.VOID, Types.VOID);

        // method Void.name(Void, Void?) : Void
        methodTable.put("name", methodBodies[4], Types.VOID, Types.VOID, Types.VOID, Types.NULLABLE_VOID);

        // method Void.name(Integer?) : Void
        methodTable.put("name", methodBodies[5], Types.VOID, Types.VOID, Types.NULLABLE_INTEGER);

        // method Void.name(Float, Float?) : Void
        methodTable.put("name", methodBodies[6], Types.VOID, Types.VOID, Types.FLOAT, Types.NULLABLE_FLOAT);

        // method Void.name(Float?, Float) : Void
        methodTable.put("name", methodBodies[7], Types.VOID, Types.VOID, Types.NULLABLE_FLOAT, Types.FLOAT);

        // method Void.name(Void, Float?, Float) : Void
        methodTable.put("name", methodBodies[8], Types.VOID, Types.VOID, Types.VOID, Types.NULLABLE_FLOAT, Types.FLOAT);
    }

    @Test
    public void methodWithoutParameter() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        Method method = methodTable.lookup("name", Types.VOID, Collections.emptyList());

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(0L));
    }

    @Test
    public void methodWithOneParameter_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void)
        Method method = methodTable.lookup("name", Types.VOID, Collections.singletonList(Types.VOID));

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(1L));
    }

    @Test
    public void methodWithThreeParameter_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void)
        Method method = methodTable.lookup("name", Types.VOID, Arrays.asList(Types.VOID, Types.FLOAT, Types.FLOAT));

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(8L));
    }

    @Test
    public void methodWithTwoParameters_1() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void, Void)
        Method method = methodTable.lookup("name", Types.VOID, Arrays.asList(Types.VOID, Types.VOID));

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(3L));
    }

    @Test
    public void methodWithTwoParameters_2() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Void, Void?)
        Method method = methodTable.lookup("name", Types.VOID, Arrays.asList(Types.VOID, Types.NULLABLE_VOID));

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(4L));
    }

    @Test
    public void methodWithOneParameter_3() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name(Integer)
        Method method = methodTable.lookup("name", Types.VOID, Collections.singletonList(Types.INTEGER));

        assertThat(method.apply(null))
                .isEqualTo(DirectValue.build(5L));
    }

    @Test
    public void methodNotFound() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        try {
            methodTable.lookup("name", Types.VOID, Collections.singletonList(Types.BOOLEAN));
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
            methodTable.lookup("name", Types.VOID, Arrays.asList(Types.FLOAT, Types.FLOAT));
        } catch (Exception e) {
            assertThat(e)
                    .hasMessage("Too many potential matches (2).");
            return;
        }

        fail("Exception expected");
    }

    @Test
    public void incompatibleParameter() {
        // GIVEN
        // @Before

        // WHEN
        // Void.name()
        try {
            methodTable.lookup("name", Types.VOID, Arrays.asList(Types.VOID, Types.FLOAT, Types.INTEGER));
        } catch (Exception e) {
            assertThat(e)
                    .hasMessage("Method not found.");
            return;
        }

        fail("Exception expected");
    }

    public void fail(String message) {
        //noinspection ThrowableResultOfMethodCallIgnored see method's javadoc
        Fail.fail(message);
    }
}
