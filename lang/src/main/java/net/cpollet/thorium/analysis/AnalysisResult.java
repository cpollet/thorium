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

package net.cpollet.thorium.analysis;

import net.cpollet.thorium.analysis.exceptions.ThoriumSemanticException;
import net.cpollet.thorium.types.Type;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.List;

/**
 * @author Christophe Pollet
 */
public class AnalysisResult {

    private final ParseTreeProperty<Type> nodesTypes;
    private final List<ThoriumSemanticException> exceptions;

    public AnalysisResult(ParseTreeProperty<Type> nodesTypes, List<ThoriumSemanticException> exceptions) {
        this.nodesTypes = nodesTypes;
        this.exceptions = exceptions;
    }

    public ParseTreeProperty<Type> getNodesTypes() {
        return nodesTypes;
    }

    public List<ThoriumSemanticException> getExceptions() {
        return exceptions;
    }
}
