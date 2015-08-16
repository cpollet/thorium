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

import ch.pollet.thorium.evaluation.EvaluationContext;
import ch.pollet.thorium.evaluation.Evaluator;
import ch.pollet.thorium.antlr.ThoriumLexer;
import ch.pollet.thorium.antlr.ThoriumParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author Christophe Pollet
 */
public class Thorium {
    public static void main(String argv[]) throws Exception {
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream("1+1");
        // create a lexer that feeds off of input CharStream
        ThoriumLexer lexer = new ThoriumLexer(input);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        ThoriumParser parser = new ThoriumParser(tokens);

        ParseTree tree = parser.compilationUnit(); // begin parsing at compilationUnit rule

        System.out.println(tree.toStringTree(parser)); // print LISP-style tree

        // Create a generic parse tree walker that can trigger callbacks
        ParseTreeWalker walker = new ParseTreeWalker();

        EvaluationContext evaluationContext = EvaluationContext.createEmpty();
        Evaluator evaluator = new Evaluator(evaluationContext);

        // Walk the tree created during the parse, trigger callbacks
        walker.walk(evaluator, tree);

        System.out.println(evaluationContext.popStack());
        System.out.println(evaluationContext.popStack());

        System.out.println(); // print a \n after translation
    }
}
