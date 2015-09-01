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
public class MethodNotFoundException extends ThoriumException {
    private static final String METHOD_NOT_FOUND = "Method {0}({1}) not implemented on {2}.";


    public MethodNotFoundException(String message) {
        super(message);
    }

    public static MethodNotFoundException build(Token token, String methodName, Type leftType, Type... parametersTypes) {
        return new MethodNotFoundException(formatMessage(METHOD_NOT_FOUND, location(token), methodName, CollectionUtils.concat(parametersTypes), leftType.toString()));
    }
}
