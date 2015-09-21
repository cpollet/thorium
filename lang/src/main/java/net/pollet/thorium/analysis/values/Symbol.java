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

package net.pollet.thorium.analysis.values;

import net.pollet.thorium.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/**
 * @author Christophe Pollet
 */
public class Symbol {
    private final Token token;
    private final String name;
    private Type type;
    private ParserRuleContext definedAt;

    public Symbol(String name, Token token, Type type) {
        this.name = name;
        this.token = token;
        this.type = type;
        this.definedAt = null;
    }

    public enum SymbolKind {
        VARIABLE, CONSTANT
    }

    public boolean isWritable() {
        return true;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public ParserRuleContext getDefinedAt() {
        return definedAt;
    }

    public void setDefinedAt(ParserRuleContext definedAt) {
        this.definedAt = definedAt;
    }

    public Token getToken() {
        return token;
    }

    public void lock() {
        // nothing
    }

    public static Symbol create(String name, SymbolKind kind, Type type, Token token) {
        switch (kind) {
            case VARIABLE:
                return new Symbol(name, token, type);
            case CONSTANT:
                return new ConstantSymbol(name, token, type);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public String toString() {
        return name + ":" + type.toString();
    }
}
