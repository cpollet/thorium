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

import ch.pollet.thorium.antlr.grammar.jbehave.StoryContext;
import ch.pollet.thorium.evaluation.EvaluationContext;
import ch.pollet.thorium.evaluation.Evaluator;
import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.types.FloatType;
import ch.pollet.thorium.values.types.IntegerType;
import ch.pollet.thorium.values.types.Type;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jbehave.core.annotations.*;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class BaseSteps {
    protected final StoryContext storyContext;

    public BaseSteps(StoryContext storyContext) {
        this.storyContext = storyContext;
    }

    @When("being executed")
    public void execute() {
        ParseTreeWalker walker = new ParseTreeWalker();
        storyContext.evaluationContext = EvaluationContext.createEmpty();
        Evaluator evaluator = new Evaluator(storyContext.evaluationContext);

        try {
            walker.walk(evaluator, storyContext.tree);
        } catch (Exception e) {
            storyContext.exception = e;
        }
    }

    @Then("the result is $value of type $type")
    @Alias("the result is <value> of type <type>")
    public void theResultIs(@Named("value") String expectedValue, @Named("type") String expectedType) {
        Object value = storyContext.evaluationContext.popStack();

        assertThat(value).isEqualTo(toTypeValue(expectedValue, expectedType));
    }

    @Then("the symbol table contains $symbols")
    @Alias("the symbol table contains <symbols>")
    public void symbolTableContains(@Named("symbols") String expectedSymbols) {
        String[] symbols = expectedSymbols.split(",");

        for (String symbol : symbols) {
            assertThat(storyContext.evaluationContext.lookupSymbol(symbol)).isNotNull();
        }
    }

    @Then("the symbol $symbol has value $value of type $type")
    @Alias("the symbol <symbol> has value <value> of type <type>")
    public void symbolHasValue(@Named("symbol") String expectedSymbol, @Named("value") String expectedValue, @Named("type") String expectedType) throws ClassNotFoundException {
        Symbol symbol = storyContext.evaluationContext.lookupSymbol(expectedSymbol);
        assertThat(symbol.getType()).isEqualTo(toTypeClass(expectedType));
        assertThat(symbol.getValue()).isEqualTo(toTypeValue(expectedValue, expectedType));
    }

    @Then("the symbols $symbols have values $values of types $types")
    @Alias("the symbols <symbols> have values <values> of types <types>")
    public void symbolsHaveValues(@Named("symbols") String expectedSymbols, @Named("values") String expectedValues, @Named("types") String expectedTypes) {
        String[] symbols = expectedSymbols.split(",");
        String[] values = expectedValues.split(",");
        String[] types = expectedTypes.split(",");

        for (int i = 0; i < symbols.length; i++) {
            assertThat(storyContext.evaluationContext.lookupSymbol(symbols[i]).getType())
                    .isEqualTo(toTypeClass(types[i]));
            assertThat(storyContext.evaluationContext.lookupSymbol(symbols[i]).getValue())
                    .isEqualTo(toTypeValue(values[i], types[i]));
        }
    }

    @Then("the exception $exception is thrown with message $message")
    @Alias("the exception <exception> is thrown with message <message>")
    public void exceptionIsThrown(@Named("exception") String exception, @Named("message") String message) throws ClassNotFoundException {
        assertThat(storyContext.exception)
                .isNotNull()
                .isInstanceOf((Class<? extends Throwable>) Class.forName(exception))
                .hasMessage(message);
    }

    protected Class<? extends Type> toTypeClass(String type) {
        switch (type) {
            case "IntegerType":
                return IntegerType.class;
            case "FloatType":
                return FloatType.class;
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }

    protected Type toTypeValue(String value, String type) {
        switch (type) {
            case "IntegerType":
                return new IntegerType(Long.parseLong(value));
            case "FloatType":
                return new FloatType(Double.parseDouble(value));
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }
}
