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

package ch.pollet.thorium;

import ch.pollet.thorium.analysis.SemanticAnalyser;
import ch.pollet.thorium.antlr.ThoriumLexer;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.evaluation.EvaluationContext;
import ch.pollet.thorium.evaluation.SymbolTable;
import ch.pollet.thorium.evaluation.VisitorEvaluator;
import ch.pollet.thorium.values.Symbol;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Christophe Pollet
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class IntegrationTest {
    @Test
    public void iterativeFibonacci() throws IOException {
        // GIVEN
        ANTLRInputStream input = new ANTLRInputStream(IntegrationTest.class.getClassLoader().getResourceAsStream("iterative_fibonacci.th"));
        ThoriumLexer lexer = new ThoriumLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ThoriumParser parser = new ThoriumParser(tokens);

        // WHEN
        ParseTree tree = parser.compilationUnit();

        SemanticAnalyser semanticAnalyser = new SemanticAnalyser(new SymbolTable<>(), parser, tree);
        semanticAnalyser.analyze();

        EvaluationContext evaluationContext = EvaluationContext.createEmpty();
        VisitorEvaluator evaluator = new VisitorEvaluator(evaluationContext);
        evaluator.visit(tree);

        // THEN
        Symbol result = evaluationContext.lookupSymbol("result");
        assertThat((Long) (result.value().internalValue()))
                .isEqualTo(34L);
    }
}
