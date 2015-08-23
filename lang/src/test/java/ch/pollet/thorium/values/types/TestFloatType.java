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

package ch.pollet.thorium.values.types;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestFloatType {
    @Test
    public void operatorPlusFloatType() {
        // GIVEN
        FloatType left = new FloatType(1.0);
        FloatType right = new FloatType(2.0);

        // WHEN
        FloatType result = left.operatorPlus(right);

        // THEN
        assertThat(result).isEqualTo(new FloatType(3.0));
    }

    @Test
    public void operatorPlusIntegerType() {
        // GIVEN
        FloatType left = new FloatType(1.0);
        IntegerType right = new IntegerType(2L);

        // WHEN
        FloatType result = left.operatorPlus(right);

        // THEN
        assertThat(result).isEqualTo(new FloatType(3.0));
    }

    @Test
    public void operatorMultiplyFloatType() {
        // GIVEN
        FloatType left = new FloatType(1.0);
        FloatType right = new FloatType(2.0);

        // WHEN
        FloatType result = left.operatorMultiply(right);

        // THEN
        assertThat(result).isEqualTo(new FloatType(2.0));
    }

    @Test
    public void operatorMultiplyIntegerType() {
        // GIVEN
        FloatType left = new FloatType(1.0);
        IntegerType right = new IntegerType(2L);

        // WHEN
        FloatType result = left.operatorMultiply(right);

        // THEN
        assertThat(result).isEqualTo(new FloatType(2.0));
    }
}