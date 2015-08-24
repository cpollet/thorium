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
import ch.pollet.thorium.evaluation.VisitorEvaluator;
import ch.pollet.thorium.semantic.exception.SymbolNotFoundException;
import ch.pollet.thorium.values.DirectValue;
import ch.pollet.thorium.values.Symbol;
import ch.pollet.thorium.values.Value;
import ch.pollet.thorium.types.Type;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.EmptyStackException;
import java.util.Stack;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.failure;

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
        // ListenerEvaluator listenerEvaluator = new ListenerEvaluator(storyContext.evaluationContext);
        VisitorEvaluator visitorEvaluator = new VisitorEvaluator(storyContext.evaluationContext);

        try {
            visitorEvaluator.visit(storyContext.tree);
            // walker.walk(evaluator, storyContext.tree);
        } catch (Exception e) {
            if (e instanceof ParseCancellationException) {
                throw e;
            }

            if (storyContext.exceptionExpected && storyContext.exception == null) {
                storyContext.exception = e;
            } else {
                throw e;
            }
        }
    }

    @Then("the result is $value of type $type")
    @Alias("the result is <value> of type <type>")
    public void theResultIs(@Named("value") String expectedValue, @Named("type") String expectedType) {
        Value value = storyContext.evaluationContext.popStack();

        assertThat(value).isEqualTo(toValue(expectedValue, expectedType));

        storyContext.evaluationContext.pushStack(value);
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
        assertThat(symbol.type()).isEqualTo(toType(expectedType));
        assertThat(symbol.value()).isEqualTo(toValue(expectedValue, expectedType));
    }

    @Then("the symbols $symbols have values $values of types $types")
    @Alias("the symbols <symbols> have values <values> of types <types>")
    public void symbolsHaveValues(@Named("symbols") String expectedSymbols, @Named("values") String expectedValues, @Named("types") String expectedTypes) {
        String[] symbols = expectedSymbols.split(",");
        String[] values = expectedValues.split(",");
        String[] types = expectedTypes.split(",");

        for (int i = 0; i < symbols.length; i++) {
            assertThat(storyContext.evaluationContext.lookupSymbol(symbols[i]).type())
                    .isEqualTo(toType(types[i]));
            assertThat(storyContext.evaluationContext.lookupSymbol(symbols[i]).value())
                    .isEqualTo(toValue(values[i], types[i]));
        }
    }

    @Then("the symbols <undefined-symbols> are not defined")
    @Alias("the symbols $undefined-symbols are not defined")
    public void symbolsAreNotDefined(@Named("undefined-symbols") String expectedNotDefinedSymbols) {
        String[] symbols = expectedNotDefinedSymbols.split(",");

        for (String symbol : symbols) {
            try {
                storyContext.evaluationContext.lookupSymbol(symbol);
                throw failure("Symbol table contains [" + symbol + "]");
            } catch (SymbolNotFoundException e) {
                // nothing
            }
        }
    }

    @Given("exception expected")
    public void exceptionExpected() {
        storyContext.exceptionExpected = true;
    }

    @Then("the exception $exception is thrown with message $message")
    @Alias("the exception <exception> is thrown with message <message>")
    public void exceptionIsThrown(@Named("exception") String exception, @Named("message") String message) throws ClassNotFoundException {
        assertThat(storyContext.exception)
                .isNotNull()
                .isInstanceOf((Class<? extends Throwable>) Class.forName(exception))
                .hasMessage(message);

        storyContext.exception = null;
        storyContext.exceptionExpected = false;
    }

    @Then("the stack contains $count elements")
    @Alias("the stack contains <count> elements")
    public void assertStackHasSize(@Named("count") Integer expectedCount) {
        Stack<Value> stack = new Stack<>();

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                stack.push(storyContext.evaluationContext.popStack());
            }
        } catch (EmptyStackException e) {
            assertThat(stack.size()).isEqualTo(expectedCount);
            while (!stack.empty()) {
                storyContext.evaluationContext.pushStack(stack.pop());
            }
        }
    }

    protected Type toType(String type) {
        switch (type) {
            case "IntegerType":
                return Type.INTEGER;
            case "FloatType":
                return Type.FLOAT;
            case "BooleanType":
                return Type.BOOLEAN;
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }

    protected Value toValue(String value, String type) {
        switch (type) {
            case "IntegerType":
                return DirectValue.build(Long.parseLong(value));
            case "FloatType":
                return DirectValue.build(Double.parseDouble(value));
            case "BooleanType":
                switch (value) {
                    case "true":
                        return DirectValue.build(true);
                    case "false":
                        return DirectValue.build(false);
                    default:
                        throw new IllegalArgumentException("[" + value + "] is not a valid Boolean value");
                }
            case "NullType":
                return DirectValue.build();
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }
}
