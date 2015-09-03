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

package ch.pollet.thorium.antlr.grammar.jbehave.steps;

import ch.pollet.thorium.ThoriumException;
import ch.pollet.thorium.jbehave.JBehaveStoryContext;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;

import java.util.EmptyStackException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class StatementsSteps extends BaseSteps {
    public StatementsSteps(JBehaveStoryContext storyContext) {
        super(storyContext);
    }

    @Given("a list of statements $statements")
    @Alias("a list of statements <statements>")
    public void aListOfStatements(@Named("statements") String statements) {
        init();
        try {
            storyContext.thoriumCode = statements;
            storyContext.parser = createParser(statements);
            storyContext.tree = storyContext.parser.statements();
        } catch (ThoriumException | ParseCancellationException e) {
            if (storyContext.exceptionExpected && storyContext.exception == null) {
                storyContext.exception = e;
            } else {
                throw e;
            }
        }
    }

    @Then("the statement result is <result> of type <type>")
    @Alias("the statement result is $result of type $type")
    public void statementResult(@Named("result") String expectedValue, @Named("type") String expectedType) {
        Object value = storyContext.evaluationContext.lastStatementValue;

        assertThat(value).isEqualTo(toValue(expectedValue, expectedType));
    }

    @Then("the stack is empty")
    public void assertStackIsEmpty() {
        Exception expectedException = null;

        try {
            storyContext.evaluationContext.popStack();
        } catch (EmptyStackException e) {
            expectedException = e;
        }

        assertThat(expectedException).isNotNull();
    }
}
