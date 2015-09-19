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

import ch.pollet.thorium.antlr.ThoriumLexer;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.execution.ExecutionContext;
import ch.pollet.thorium.execution.ExecutionVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * @author Christophe Pollet
 */
public class Thorium {
    public static void main(String[] args) throws Exception {
        new Thorium().run();
    }

    private void run() {
        ANTLRInputStream input = new ANTLRInputStream("1+1;");
        ThoriumLexer lexer = new ThoriumLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ThoriumParser parser = new ThoriumParser(tokens);

        ParseTree tree = parser.compilationUnit();

        System.out.println(tree.toStringTree(parser));

        ExecutionContext executionContext = ExecutionContext.createEmpty();
        ExecutionVisitor executionVisitor = new ExecutionVisitor(executionContext);

        executionVisitor.visit(tree);

        System.out.println(executionContext.lastStatementValue);
        System.out.println();
    }
}
