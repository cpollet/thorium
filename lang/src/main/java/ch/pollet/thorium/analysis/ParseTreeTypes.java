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

package ch.pollet.thorium.analysis;

import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.types.Types;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public class ParseTreeTypes {
    protected Map<ParseTree, Set<Type>> annotations = new IdentityHashMap<>();

    public Set<Type> get(ParseTree node) {
        return annotations.get(node);
    }

    public void put(ParseTree node, Set<Type> value) {
        annotations.put(node, value);
    }

    public ParseTreeProperty<Type> reduce() {
        ParseTreeProperty<Type> result = new ParseTreeProperty<>();

        annotations.forEach((parseTree, types) -> {
            if (types.size() != 1) {
                result.put(parseTree, Types.NULLABLE_VOID);
            } else {
                result.put(parseTree, types.iterator().next());
            }
        });

        return result;
    }
}
