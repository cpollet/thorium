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
import ch.pollet.bubble.evaluation.EvaluationContext;
import ch.pollet.bubble.evaluation.Evaluator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestExpressionEvaluation {
    private ParseTreeWalker walker;
    private EvaluationContext evaluationContext;
    private Evaluator evaluator;

    private ParseTree parseTreeForExpression(String expression) {
        BubbleParser parser = ParserBuilder
                .create()
                .withCode(expression)
                .build();

        return parser.expression();
    }

    @Before
    public void setUp() {
        walker = new ParseTreeWalker();
        evaluationContext = EvaluationContext.createEmpty();
        evaluator = new Evaluator(evaluationContext);
    }

    @Test
    public void simpleAddition() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("1 + 1");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(2);
    }

    @Test
    public void compositeAddition() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("1 + 1 + 1");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(3);
    }

    @Test
    public void simpleMultiplication() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("2 * 2");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(4);
    }

    @Test
    public void compositeMultiplication() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("2 * 2 * 2");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(8);
    }

    @Test
    public void additionThenMultiplication() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("0 + 1 * 2");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(2);
    }

    @Test
    public void additionThenMultiplicationWithParenthesis() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("0 + (1 * 2)");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(2);
    }

    @Test
    public void multiplicationThenAddition() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("0 * 1 + 2");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(2);
    }

    @Test
    public void multiplicationThenAdditionWithParenthesis() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("0 * (1 + 2)");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(0);
    }

    @Test
    public void nestedParenthesis() {
        // GIVEN
        ParseTree tree = parseTreeForExpression("2 * (2 + 2 * (2 + 2))");

        // WHEN
        walker.walk(evaluator, tree);

        // THEN
        assertThat(evaluationContext.popStack())
                .isEqualTo(20);
    }
}
