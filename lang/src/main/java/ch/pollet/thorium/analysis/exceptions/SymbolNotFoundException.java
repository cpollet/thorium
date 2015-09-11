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

package ch.pollet.thorium.analysis.exceptions;

import ch.pollet.thorium.ThoriumException;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.utils.CollectionUtils;
import org.antlr.v4.runtime.Token;

/**
 * @author Christophe Pollet
 */
public class SymbolNotFoundException extends ThoriumSemanticException {
    private static final String METHOD_NOT_FOUND = "Method {1}({2}) not implemented on {3} on line {0}.";
    private static final String IDENTIFIER_NOT_FOUND = "Identifier {1} not found on line {0}.";

    public SymbolNotFoundException(String message) {
        super(message);
    }

    public static SymbolNotFoundException methodNotFound(Token token, String methodName, Type leftType, Type... parametersTypes) {
        return new SymbolNotFoundException(formatMessage(METHOD_NOT_FOUND, location(token), methodName, CollectionUtils.concat(parametersTypes), leftType.toString()));
    }

    public static ThoriumException identifierNotFound(Token token, String name) {
        return new SymbolNotFoundException(formatMessage(IDENTIFIER_NOT_FOUND, location(token), name));
    }
}
