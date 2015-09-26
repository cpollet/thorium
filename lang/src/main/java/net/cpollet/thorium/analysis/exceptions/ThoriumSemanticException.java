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

import java.util.Collections;
import java.util.List;

/**
 * @author Christophe Pollet
 */
public class ThoriumSemanticException extends ThoriumException {
    private final List<ThoriumException> exceptions;

    public ThoriumSemanticException(String message, List<ThoriumException> exceptions) {
        super(message);
        this.exceptions = exceptions;
    }

    public ThoriumSemanticException(String message) {
        this(message, Collections.<ThoriumException>emptyList());
    }

    public List<ThoriumException> getCauses() {
        return exceptions;
    }
}