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

package net.pollet.thorium.analysis.exceptions;

import net.pollet.thorium.ThoriumException;
import net.pollet.thorium.types.Type;
import net.pollet.thorium.utils.CollectionUtils;
import org.antlr.v4.runtime.Token;

/**
 * @author Christophe Pollet
 */
public class InvalidSymbolException extends ThoriumSemanticException {
    private static final String METHOD_NOT_FOUND = "Method {3}.{1}({2}) not implemented on line {0}.";
    private static final String IDENTIFIER_NOT_FOUND = "Identifier {1} not found on line {0}.";
    private static final String ALREADY_DEFINED = "Identifier {1} already defined on line {0} (was on line {2}).";

    public InvalidSymbolException(String message) {
        super(message);
    }

    public static InvalidSymbolException methodNotFound(Token token, String methodName, Type leftType, Type... parametersTypes) {
        return new InvalidSymbolException(formatMessage(METHOD_NOT_FOUND, location(token), methodName, CollectionUtils.concat(parametersTypes), leftType.toString()));
    }

    public static ThoriumException identifierNotFound(Token token, String name) {
        return new InvalidSymbolException(formatMessage(IDENTIFIER_NOT_FOUND, location(token), name));
    }

    public static ThoriumException alreadyDefined(Token token, String name, Token original) {
        return new InvalidSymbolException(formatMessage(ALREADY_DEFINED, location(token), name, location(original)));
    }
}
