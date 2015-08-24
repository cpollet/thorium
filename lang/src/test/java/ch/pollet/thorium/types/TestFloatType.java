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
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;
import org.junit.Ignore;
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
    public void operatorPlusFloat() {
        // GIVEN
        DirectValue left = DirectValue.build(1.0);
        DirectValue right = DirectValue.build(2.0);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(3.0));
    }

    @Test
    public void operatorPlusInteger() {
        // GIVEN
        DirectValue left = DirectValue.build(1.0);
        DirectValue right = DirectValue.build(2L);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(3.0));
    }

    @Test
    public void operatorTimesFloat() {
        // GIVEN
        DirectValue left = DirectValue.build(1.0);
        DirectValue right = DirectValue.build(2.0);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(2.0));
    }

    @Test
    public void operatorTimesInteger() {
        // GIVEN
        DirectValue left = DirectValue.build(1.0);
        DirectValue right = DirectValue.build(2L);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(2.0));
    }
}
