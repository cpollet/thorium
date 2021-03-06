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

package net.cpollet.thorium.antlr.grammar;

import net.cpollet.thorium.ThrowingErrorListener;
import net.cpollet.thorium.antlr.ThoriumLexer;
import net.cpollet.thorium.antlr.ThoriumParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * @author Christophe Pollet
 */
public class ParserBuilder {
    private String code;

    public static ParserBuilder create() {
        return new ParserBuilder();
    }

    private ParserBuilder() {
        // nothing
    }

    public ParserBuilder withCode(String code) {
        this.code = code;
        return this;
    }

    public ThoriumParser build() {
        ANTLRInputStream input = new ANTLRInputStream(code);

        ThoriumLexer lexer = new ThoriumLexer(input);
        lexer.removeErrorListeners();
        lexer.removeErrorListener(ThrowingErrorListener.INSTANCE);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        return new ThoriumParser(tokens);
    }
}
