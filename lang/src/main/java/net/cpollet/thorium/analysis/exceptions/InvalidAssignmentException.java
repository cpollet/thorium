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

import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * @author Christophe Pollet
 */
public class InvalidAssignmentException extends ThoriumSemanticException {
    private static final String INVALID_ASSIGNMENT = "Invalid assignment found on line {0}: unable to change a constant value.";

    public InvalidAssignmentException(String message, List<ThoriumSemanticException> exceptions) {
        super(message, exceptions);
    }

    public InvalidAssignmentException(String message) {
        super(message);
    }

    public static InvalidAssignmentException build(Token token) {
        return new InvalidAssignmentException(formatMessage(INVALID_ASSIGNMENT, location(token)));
    }
}
