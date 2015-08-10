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

package ch.pollet.bubble.antlr.grammar;

import ch.pollet.bubble.antlr.BubbleParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestExpression {

    private String parseTreeToString(BubbleParser parser) {
        ParseTree tree = parser.expression();
        return tree.toStringTree(parser);
    }

    @Test
    public void literalExpression() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (literal 1))");
    }

    @Test
    public void addExpression() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 + 1")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (literal 1)) + (expression (literal 1)))");
    }

    @Test
    public void multiplyExpression() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 * 1")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (literal 1)) * (expression (literal 1)))");
    }

    @Test
    public void parenthesisExpression() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("(1)")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression ( (expression (literal 1)) ))");
    }

    @Test
    public void operationOrder1() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 + 2 * 3")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (literal 1)) + (expression (expression (literal 2)) * (expression (literal 3))))");
    }

    @Test
    public void operationOrder2() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 * 2 + 3")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (expression (literal 1)) * (expression (literal 2))) + (expression (literal 3)))");
    }

    @Test
    public void operationOrder3() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 + 2 + 3")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (expression (literal 1)) + (expression (literal 2))) + (expression (literal 3)))");
    }

    @Test
    public void operationOrder4() {
        // GIVEN
        BubbleParser parser = ParserBuilder
                .create()
                .withCode("1 * 2 * 3")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(expression (expression (expression (literal 1)) * (expression (literal 2))) * (expression (literal 3)))");
    }
}
