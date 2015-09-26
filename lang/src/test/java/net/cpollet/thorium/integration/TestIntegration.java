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

package net.cpollet.thorium.integration;

import net.cpollet.thorium.ThoriumException;
import net.cpollet.thorium.analysis.SemanticAnalyser;
import net.cpollet.thorium.analysis.exceptions.ThoriumSemanticException;
import net.cpollet.thorium.antlr.ThoriumLexer;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.execution.ExecutionContext;
import net.cpollet.thorium.execution.ExecutionVisitor;
import net.cpollet.thorium.execution.values.Symbol;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestIntegration {
    private final static Logger LOG = LoggerFactory.getLogger(TestIntegration.class);

    @Test
    public void iterativeFibonacci() throws IOException {
        // GIVEN + WHEN
        ExecutionContext executionContext = eval("iterative_fibonacci.th");

        // THEN
        Symbol result = executionContext.lookupSymbol("result");
        assertThat((Long) (result.value().internalValue()))
                .isEqualTo(34L);
    }

    @Test
    public void recursiveFibonacci() throws IOException {
        // GIVEN + WHEN
        ExecutionContext executionContext = eval("recursive_fibonacci.th");

        // THEN
        Symbol result = executionContext.lookupSymbol("result");
        assertThat((Long) (result.value().internalValue()))
                .isEqualTo(34L);
    }

    @Test
    public void iterativeFactorial() throws IOException {
        // GIVEN + WHEN
        ExecutionContext executionContext = eval("iterative_factorial.th");

        // THEN
        Symbol result = executionContext.lookupSymbol("result");
        assertThat((Long) (result.value().internalValue()))
                .isEqualTo(3628800L);
    }

    @Test
    public void recursiveFactorial() throws IOException {
        // GIVEN + WHEN
        ExecutionContext executionContext = eval("recursive_factorial.th");

        // THEN
        Symbol result = executionContext.lookupSymbol("result");
        assertThat((Long) (result.value().internalValue()))
                .isEqualTo(3628800L);
    }

    private ExecutionContext eval(String program) throws IOException {
        // GIVEN
        ANTLRInputStream input = new ANTLRInputStream(TestIntegration.class.getClassLoader().getResourceAsStream(program));
        ThoriumLexer lexer = new ThoriumLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ThoriumParser parser = new ThoriumParser(tokens);

        // WHEN
        ParseTree tree = parser.compilationUnit();

        SemanticAnalyser semanticAnalyser = new SemanticAnalyser(new SymbolTable<>(), parser, tree);

        try {
            // semanticAnalyser.analyze();
        } catch (ThoriumSemanticException e) {
            for (ThoriumException thoriumException : e.getCauses()) {
                LOG.error(thoriumException.getMessage());
            }

            throw new IllegalStateException("Semantic errors occurred.");
        }

        ExecutionContext executionContext = ExecutionContext.createEmpty();
        ExecutionVisitor evaluator = new ExecutionVisitor(executionContext);
        evaluator.visit(tree);

        return executionContext;
    }
}
