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

package ch.pollet.thorium.types.jbehave.steps;

import ch.pollet.thorium.execution.Method;
import ch.pollet.thorium.execution.MethodMatcher;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Value;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public abstract class BaseSteps {
    private Value left;
    private Value right;
    private String methodName;
    private Method method;
    private Value result;
    private Class<? extends Exception> expectedExceptionType;
    private Exception exception;

    @Given("method is <left> <method> <right>")
    public void methodDefinition(@Named("left") String left, @Named("method") String method, @Named("right") String right) {
        this.left = decodeValue(left);
        this.right = decodeValue(right);
        this.methodName = method;
    }

    @SuppressWarnings("unchecked")
    @Given("an exception <exception> is expected")
    public void exceptionExpected(@Named("exception") String exception) throws ClassNotFoundException {
        this.expectedExceptionType = (Class<? extends Exception>) Class.forName(exception);
    }

    @When("decode method")
    public void decodeMethod() {
        try {
            method = left.type().lookupMethod(new MethodMatcher(methodName, right.type()));
        } catch (Exception e) {
            if (expectedExceptionType != null && e.getClass().equals(expectedExceptionType)) {
                this.exception = e;
            } else {
                throw e;
            }
        }
    }

    @When("evaluate")
    public void evaluate() {
        result = method.apply(left, right);
    }

    @Then("the result is <result>")
    public void assertResult(@Named("result") String expectedResult) {
        assertThat(result).isEqualTo(decodeValue(expectedResult));
    }

    @Then("the result type is <type>")
    public void assertResultType(@Named("type") String type) {
        assertThat(method.getType().toString())
                .isEqualTo(type);
    }

    @Then("the method is not found")
    public void assertMethodNotFound() {
        assertThat(method).isNull();
    }

    private Value decodeValue(String value) {
        switch (value) {
            case "true":
                return DirectValue.build(true);
            case "false":
                return DirectValue.build(false);
            case "Boolean":
                return DirectValue.build(Type.BOOLEAN);
            case "Integer":
                return DirectValue.build(Type.INTEGER);
            case "Float":
                return DirectValue.build(Type.FLOAT);
        }

        try {
            return DirectValue.build(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return DirectValue.build(Double.parseDouble(value));
        }
    }
}
