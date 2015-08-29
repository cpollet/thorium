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

package ch.pollet.thorium.analysis.jbehave.steps;

import ch.pollet.thorium.analysis.TypeAnalysisListener;
import ch.pollet.thorium.jbehave.JBehaveStoryContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class TypesAnalysisSteps {
    private final JBehaveStoryContext storyContext;

    public TypesAnalysisSteps(JBehaveStoryContext storyContext) {
        this.storyContext = storyContext;
    }

    @When("types are attached to nodes")
    public void attachTypes() {
        ParseTreeWalker walker = new ParseTreeWalker();

        storyContext.listener = new TypeAnalysisListener(storyContext.parser);

        walker.walk(storyContext.listener, storyContext.tree);
    }

    @Then("root node is of types <types>")
    @Alias("root node is of types $types")
    public void assertRootNodeIsOfTypes(@Named("types") String types) {
        assertThat(storyContext.listener.getNodeType(storyContext.tree).toString())
                .isEqualTo(types);
    }
}
