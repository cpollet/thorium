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

import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.antlr.grammar.ParserBuilder;
import ch.pollet.thorium.antlr.grammar.jbehave.StoryContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class StatementsSteps extends BaseSteps {
    public StatementsSteps(StoryContext storyContext) {
        super(storyContext);
    }

    @Given("a list of statements $statements")
    @Alias("a list of statements <statements>")
    public void aListOfStatements(@Named("statements") String statements) {
        storyContext.tree = parseTreeForStatements(statements);
    }

    private ParseTree parseTreeForStatements(String expression) {
        ThoriumParser parser = ParserBuilder
                .create()
                .withCode(expression)
                .build();

        try {
            return parser.statements();
        } catch (Exception e) {
            storyContext.exception = e;
            return null;
        }
    }
}
