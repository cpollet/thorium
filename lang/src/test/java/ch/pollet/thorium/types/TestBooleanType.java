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
import ch.pollet.thorium.values.Variable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestBooleanType {
    @Test
    public void operatorPlusBoolean() {
        // GIVEN
        DirectValue left = DirectValue.build(true);
        DirectValue right = DirectValue.build(false);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(true));
    }

    @Test
    public void operatorPlusTrueWithBooleanNoValue() {
        // GIVEN
        DirectValue left = DirectValue.build(true);
        Value right = new Variable("var", Type.BOOLEAN);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(true));
    }

    @Test
    public void operatorPlusFalseWithBooleanNoValue() {
        // GIVEN
        DirectValue left = DirectValue.build(false);
        Value right = new Variable("var", Type.BOOLEAN);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build());
    }

    @Test
    public void operatorPlusBooleanNoValueWithTrue() {
        // GIVEN
        Value left = new Variable("var", Type.BOOLEAN);
        DirectValue right = DirectValue.build(true);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(true));
    }

    @Test
    public void operatorPlusBooleanNoValueWithFalse() {
        // GIVEN
        Value left = new Variable("var", Type.BOOLEAN);
        DirectValue right = DirectValue.build(false);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("+", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build());
    }

    @Test
    public void operatorMultiplyBoolean() {
        // GIVEN
        DirectValue left = DirectValue.build(true);
        DirectValue right = DirectValue.build(false);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(false));
    }

    @Test
    public void operatorMultiplyTrueWithBooleanNoValue() {
        // GIVEN
        DirectValue left = DirectValue.build(true);
        Value right = new Variable("var", Type.BOOLEAN);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build());
    }

    @Test
    public void operatorMultiplyFalseWithBooleanNoValue() {
        // GIVEN
        DirectValue left = DirectValue.build(false);
        Value right = new Variable("var", Type.BOOLEAN);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(false));
    }

    @Test
    public void operatorMultiplyBooleanNoValueWithTrue() {
        // GIVEN
        Value left = new Variable("var", Type.BOOLEAN);
        DirectValue right = DirectValue.build(true);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build());
    }

    @Test
    public void operatorMultiplyBooleanNoValueWithFalse() {
        // GIVEN
        Value left = new Variable("var", Type.BOOLEAN);
        DirectValue right = DirectValue.build(false);

        // WHEN
        Value result = left.type().lookupMethod(new MethodMatcher("*", right.type())).apply(left, right);

        // THEN
        assertThat(result).isEqualTo(DirectValue.build(false));
    }
}
