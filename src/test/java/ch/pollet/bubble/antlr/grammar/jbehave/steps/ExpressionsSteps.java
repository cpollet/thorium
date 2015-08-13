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

package ch.pollet.bubble.antlr.grammar.jbehave.steps;

import ch.pollet.bubble.antlr.BubbleParser;
import ch.pollet.bubble.antlr.grammar.ParserBuilder;
import ch.pollet.bubble.evaluation.EvaluationContext;
import ch.pollet.bubble.evaluation.Evaluator;
import ch.pollet.bubble.types.FloatType;
import ch.pollet.bubble.types.IntegerType;
import ch.pollet.bubble.types.Type;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jbehave.core.annotations.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class ExpressionsSteps {
    private ParseTree tree;
    private EvaluationContext evaluationContext;

    @Given("an expression $expression")
    @Alias("an expression <expression>")
    public void anExpression(@Named("expression") String expression) {
        tree = parseTreeForExpression(expression);
    }

    private ParseTree parseTreeForExpression(String expression) {
        BubbleParser parser = ParserBuilder
                .create()
                .withCode(expression)
                .build();

        return parser.expression();
    }

    @When("being executed")
    public void execute() {
        ParseTreeWalker walker = new ParseTreeWalker();
        evaluationContext = EvaluationContext.createEmpty();
        Evaluator evaluator = new Evaluator(evaluationContext);
        walker.walk(evaluator, tree);
    }

    @Then("the result is $expectedValue of type $type")
    @Alias("the result is <expectedValue> of type <type>")
    public void theResultIs(@Named("expectedValue") String expectedValue, @Named("type") String type) {
        Object value = evaluationContext.popStack();

        assertThat(value).isEqualTo(toTypeValue(expectedValue, type));
    }

    private Type toTypeValue(String value, String type) {
        switch (type) {
            case "IntegerType":
                return new IntegerType(Long.parseLong(value));
            case "FloatType":
                return new FloatType(Double.parseDouble(value));
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }


}
