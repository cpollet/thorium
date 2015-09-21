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

package net.pollet.thorium.antlr.grammar;

import net.pollet.thorium.antlr.ThoriumParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(JUnit4.class)
public class TestStatements {
    private String parseTreeToString(ThoriumParser parser) {
        ParseTree tree = parser.statements();
        return tree.toStringTree(parser);
    }

    @Test
    public void literalExpression() {
        // GIVEN
        ThoriumParser parser = ParserBuilder
                .create()
                .withCode("1;2;3;")
                .build();

        // WHEN + THEN
        assertThat(parseTreeToString(parser))
                .isEqualTo("(statements (statement (expressionStatement (expression (literal 1)) ;)) (statement (expressionStatement (expression (literal 2)) ;)) (statement (expressionStatement (expression (literal 3)) ;)))");
    }
}
