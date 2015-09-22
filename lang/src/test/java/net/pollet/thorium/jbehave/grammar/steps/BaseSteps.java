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

package net.pollet.thorium.jbehave.grammar.steps;

import net.pollet.thorium.ThoriumException;
import net.pollet.thorium.ThrowingErrorListener;
import net.pollet.thorium.antlr.ThoriumParser;
import net.pollet.thorium.antlr.grammar.ParserBuilder;
import net.pollet.thorium.data.method.MethodNotFoundException;
import net.pollet.thorium.data.symbol.SymbolTable;
import net.pollet.thorium.execution.ExecutionContext;
import net.pollet.thorium.execution.ExecutionVisitor;
import net.pollet.thorium.execution.values.Symbol;
import net.pollet.thorium.jbehave.JBehaveStoryContext;
import net.pollet.thorium.types.Types;
import net.pollet.thorium.values.DirectValue;
import net.pollet.thorium.values.Value;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.Stack;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public abstract class BaseSteps {
    protected final JBehaveStoryContext storyContext;

    public BaseSteps(JBehaveStoryContext storyContext) {
        this.storyContext = storyContext;
    }

    protected void init() {
        storyContext.evaluationBaseScope = new SymbolTable<>();
        storyContext.analysisBaseScope = new SymbolTable<>();
    }

    @When("being executed")
    public void execute() {
        storyContext.executionContext = ExecutionContext.createEmpty();
        ExecutionVisitor executionVisitor = new ExecutionVisitor(storyContext.executionContext);

        try {
            executionVisitor.visit(storyContext.tree);
        } catch (ThoriumException | IllegalStateException | AssertionError | MethodNotFoundException e) {
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
        Value value = storyContext.executionContext.popStack();

        assertThat(value).isEqualTo(toValue(expectedValue, expectedType));

        storyContext.executionContext.pushStack(value);
    }

    @Then("the symbol table contains $symbols")
    @Alias("the symbol table contains <symbols>")
    public void symbolTableContains(@Named("symbols") String expectedSymbols) {
        String[] symbols = expectedSymbols.split(",");

        for (String symbol : symbols) {
            assertThat(storyContext.executionContext.lookupSymbol(symbol)).isNotNull();
        }
    }

    @Then("the symbol $symbol has value $value of type $type")
    @Alias("the symbol <symbol> has value <value> of type <type>")
    public void symbolHasValue(@Named("symbol") String expectedSymbol, @Named("value") String expectedValue, @Named("type") String expectedType) {
        Symbol symbol = storyContext.executionContext.lookupSymbol(expectedSymbol);
        assertThat(symbol.type().toString()).isEqualTo(expectedType);
        assertThat(symbol.value()).isEqualTo(toValue(expectedValue, expectedType));
    }

    @Then("the symbols $symbols have values $values of types $types")
    @Alias("the symbols <symbols> have values <values> of types <types>")
    public void symbolsHaveValues(@Named("symbols") String expectedSymbols, @Named("values") String expectedValues, @Named("types") String expectedTypes) {
        String[] symbols = expectedSymbols.split(",");
        String[] values = expectedValues.split(",");
        String[] types = expectedTypes.split(",");

        for (int i = 0; i < symbols.length; i++) {
            assertThat(storyContext.executionContext.lookupSymbol(symbols[i]).type().toString())
                    .isEqualTo(types[i]);
            assertThat(storyContext.executionContext.lookupSymbol(symbols[i]).value())
                    .isEqualTo(toValue(values[i], types[i]));
        }
    }

    @Then("the symbols <undefined-symbols> are not defined")
    @Alias("the symbols $undefined-symbols are not defined")
    public void symbolsAreNotDefined(@Named("undefined-symbols") String expectedNotDefinedSymbols) {
        String[] symbols = expectedNotDefinedSymbols.split(",");

        for (String symbol : symbols) {
            assertThat(storyContext.executionContext.symbolDefined(symbol))
                    .overridingErrorMessage("Symbol table contains [" + symbol + "]")
                    .isFalse();
        }
    }

    @Given("exception expected")
    public void exceptionExpected() {
        storyContext.exceptionExpected = true;
    }

    @SuppressWarnings("unchecked")
    @Then("the exception <exception> is thrown")
    @Alias("the exception $exception$ is thrown")
    public void exceptionIsThrown(@Named("exception") String exception) throws ClassNotFoundException {
        if (exception.startsWith(".")) {
            exception = "net.pollet.thorium" + exception;
        }

        assertThat(storyContext.exception)
                .overridingErrorMessage("Expected exception not thrown")
                .isNotNull();
        assertThat(storyContext.exception)
                .isInstanceOf((Class<? extends Throwable>) Class.forName(exception));

        storyContext.exception = null;
        storyContext.exceptionExpected = false;
        storyContext.exceptionsExpected = 0;
    }

    @SuppressWarnings("unchecked")
    @Then(value = "the exception <exception> is thrown with message matching <message>", priority = 2)
    @Alias("the exception $exception is thrown with message matching $message")
    public void exceptionIsThrownWithMessageMatching(@Named("exception") String exception, @Named("message") String message) throws ClassNotFoundException {
        if (exception.startsWith(".")) {
            exception = "net.pollet.thorium" + exception;
        }

        assertThat(storyContext.exception)
                .overridingErrorMessage("Expected exception not thrown")
                .isNotNull();
        assertThat(storyContext.exception)
                .isInstanceOf((Class<? extends Throwable>) Class.forName(exception));
        assertThat(storyContext.exception.getMessage())
                .matches(message);

        storyContext.exception = null;
        storyContext.exceptionExpected = false;
        storyContext.exceptionsExpected = 0;
    }

    @SuppressWarnings("unchecked")
    @Then(value = "the exception $exception is thrown with message $message", priority = 1)
    @Alias("the exception <exception> is thrown with message <message>")
    public void exceptionIsThrown(@Named("exception") String exception, @Named("message") String message) throws ClassNotFoundException {
        assertThat(storyContext.exception)
                .overridingErrorMessage("Expected exception not thrown")
                .isNotNull();
        assertThat(storyContext.exception)
                .isInstanceOf((Class<? extends Throwable>) Class.forName(exception))
                .hasMessage(message);

        storyContext.exception = null;
        storyContext.exceptionExpected = false;
        storyContext.exceptionsExpected = 0;
    }

    @Then("no exceptions were thrown")
    public void assertNoExceptionsWereThrown() {
        assertThat(storyContext.exception)
                .isNull();
    }

    @Then("the stack contains $count elements")
    @Alias("the stack contains <count> elements")
    public void assertStackHasSize(@Named("count") Integer expectedCount) {
        Stack<Value> stack = new Stack<>();

        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                stack.push(storyContext.executionContext.popStack());
            }
        } catch (EmptyStackException | NoSuchElementException e) {
            assertThat(stack.size()).isEqualTo(expectedCount);
            while (!stack.empty()) {
                storyContext.executionContext.pushStack(stack.pop());
            }
        }
    }

    protected ThoriumParser createParser(String code) {
        ThoriumParser parser = ParserBuilder
                .create()
                .withCode(code)
                .build();

        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);

        return parser;
    }

    protected Value toValue(String value, String type) {
        switch (type) {
            case "Integer":
                return DirectValue.build(Long.parseLong(value));
            case "Integer?":
                return DirectValue.build(Types.NULLABLE_INTEGER);
            case "Float":
                return DirectValue.build(Double.parseDouble(value));
            case "Float?":
                return DirectValue.build(Types.NULLABLE_FLOAT);
            case "Boolean":
                switch (value) {
                    case "true":
                        return DirectValue.build(true);
                    case "false":
                        return DirectValue.build(false);
                    default:
                        throw new IllegalArgumentException("[" + value + "] is not a valid Boolean value");
                }
            case "Boolean?":
                return DirectValue.build(Types.NULLABLE_BOOLEAN);
            case "Void":
                return DirectValue.build();
            case "Void?":
                return DirectValue.build(Types.NULLABLE_VOID);
        }

        throw new IllegalArgumentException("[" + type + "] is not a valid type");
    }
}
