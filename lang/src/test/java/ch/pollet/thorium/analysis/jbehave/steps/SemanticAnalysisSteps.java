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

import ch.pollet.thorium.ThoriumException;
import ch.pollet.thorium.analysis.SemanticAnalyser;
import ch.pollet.thorium.analysis.exceptions.ThoriumSemanticException;
import ch.pollet.thorium.analysis.values.Symbol;
import ch.pollet.thorium.antlr.grammar.jbehave.steps.BaseSteps;
import ch.pollet.thorium.jbehave.JBehaveStoryContext;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
public class SemanticAnalysisSteps extends BaseSteps {
    public SemanticAnalysisSteps(JBehaveStoryContext storyContext) {
        super(storyContext);
    }

    @Given("a compilation unit <unit>")
    @Alias("a compilation unit $unit")
    public void aCompilationUnit(@Named("unit") String unit) {
        init();
        try {
            storyContext.thoriumCode = unit;
            storyContext.parser = createParser(unit);
            storyContext.tree = storyContext.parser.compilationUnit();
        } catch (Exception e) {
            if (!(e instanceof ThoriumException)) {
                throw e;
            }

            if (storyContext.exceptionExpected && storyContext.exception == null) {
                storyContext.exception = e;
            } else {
                throw e;
            }
        }
    }

    @When("types are attached to nodes")
    public void attachTypes() throws Exception {
        SemanticAnalyser semanticAnalyser = new SemanticAnalyser(storyContext.analysisBaseScope, storyContext.parser, storyContext.tree);

        try {
            storyContext.types = semanticAnalyser.analyze();
        } catch (ThoriumSemanticException e) {
            if (storyContext.exceptionExpected && storyContext.exception == null) {
                if (e.getCauses().size() == 1) {
                    storyContext.exception = e.getCauses().get(0);
                } else {
                    throw new IllegalStateException("Size was " + e.getCauses().size(), e);
                }
            }
        } catch (ThoriumException e) {
            if (storyContext.exceptionExpected && storyContext.exception == null) {
                storyContext.exception = e;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Then("root node is of type <type>")
    @Alias("root node is of type $type")
    public void assertRootNodeIsOfTypes(@Named("type") String type) {
        assertThat(storyContext.types.get(storyContext.tree).toString())
                .isEqualTo(type);
    }

    @Then("the symbol <symbol> is of type <type>")
    @Alias("the symbol $symbol is of type $type")
    public void assertSymbolIsOfType(@Named("symbol") String symbolName, @Named("type") String type) {
        Symbol symbol = storyContext.analysisBaseScope.get(symbolName);

        assertThat(symbol)
                .isNotNull();

        assertThat(symbol.getType().toString())
                .isEqualTo(type);
    }
}