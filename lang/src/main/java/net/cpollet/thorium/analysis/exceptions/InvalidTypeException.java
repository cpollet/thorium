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

package net.cpollet.thorium.analysis.exceptions;

import net.cpollet.thorium.ThoriumException;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.utils.CollectionUtils;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.utils.CollectionUtils;
import org.antlr.v4.runtime.Token;

import java.util.Collection;

/**
 * @author Christophe Pollet
 */
public class InvalidTypeException extends ThoriumSemanticException {
    private static final String INVALID_TYPE = "Invalid type found on line {0}: expected {1} but got {2}.";
    private static final String AMBIGUOUS_TYPE = "Ambiguous type found on line {0}: expected only one, but got {1}.";
    private static final String TYPE_EXPECTED = "Type expected, but got Void on line {0}.";
    private static final String NOT_COMPATIBLE = "Incompatible types found on line {0}: {1} is no assignable to {2}.";

    public InvalidTypeException(String message) {
        super(message);
    }

    public static InvalidTypeException invalidType(Token token, Type expected, Type actual) {
        return new InvalidTypeException(ThoriumException.formatMessage(INVALID_TYPE, ThoriumException.location(token), expected.toString(), actual.toString()));
    }

    public static InvalidTypeException ambiguousType(Token token, Collection<Type> types) {
        return new InvalidTypeException(ThoriumException.formatMessage(AMBIGUOUS_TYPE, ThoriumException.location(token), CollectionUtils.concat(types)));
    }

    public static InvalidTypeException typeExpected(Token token) {
        return new InvalidTypeException(ThoriumException.formatMessage(TYPE_EXPECTED, ThoriumException.location(token)));
    }

    public static InvalidTypeException notCompatible(Token token, Type right, Type left) {
        return new InvalidTypeException(ThoriumException.formatMessage(NOT_COMPATIBLE, ThoriumException.location(token), right.toString(), left.toString()));
    }
}
