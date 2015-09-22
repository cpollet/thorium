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

package net.cpollet.thorium;

import org.antlr.v4.runtime.Token;

import java.text.MessageFormat;

/**
 * @author Christophe Pollet
 */
public abstract class ThoriumException extends RuntimeException {
    public ThoriumException(String message) {
        super(message);
    }

    protected static String location(Token token) {
        return token.getLine() + ":" + (token.getCharPositionInLine() + 1) + " (" + token.getText() + ")";
    }

    protected static String formatMessage(String message, String... parameters) {
        return MessageFormat.format(message, parameters);
    }
}
