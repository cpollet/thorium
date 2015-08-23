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

import ch.pollet.thorium.ThrowingErrorListener;
import ch.pollet.thorium.antlr.grammar.ParserBuilder;
import ch.pollet.thorium.antlr.grammar.jbehave.StoryContext;
import ch.pollet.thorium.antlr.ThoriumParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;

/**
 * @author Christophe Pollet
 */
public class ExpressionsSteps extends BaseSteps {
    public ExpressionsSteps(StoryContext storyContext) {
        super(storyContext);
    }

    @Given("an expression $expression")
    @Alias("an expression <expression>")
    public void anExpression(@Named("expression") String expression) {
        storyContext.tree = parseTreeForExpression(expression);
    }

    private ParseTree parseTreeForExpression(String expression) {
        ThoriumParser parser = ParserBuilder
                .create()
                .withCode(expression)
                .build();

        parser.removeErrorListeners();
        parser.addErrorListener(ThrowingErrorListener.INSTANCE);

        return parser.expression();
    }
}