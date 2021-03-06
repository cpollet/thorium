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

package net.cpollet.thorium.analysis.listener;

import net.cpollet.thorium.analysis.AnalysisContext;
import net.cpollet.thorium.analysis.ObserverRegistry;
import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.analysis.exceptions.InvalidSymbolException;
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.analysis.exceptions.ThoriumSemanticException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.method.MethodTable;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public abstract class BaseListener {
    private final ParseTreeListener parseTreeListener;
    private final AnalysisContext analysisContext;
    private final ObserverRegistry<Symbol> symbolObserverRegistry;
    private final ObserverRegistry<ParserRuleContext> nodeObserverRegistry;
    private final ObserverRegistry<String> methodObserverRegistry;

    public static class Observers {
        private final ObserverRegistry<ParserRuleContext> nodeObserverRegistry;
        private final ObserverRegistry<Symbol> symbolObserverRegistry;
        private final ObserverRegistry<String> methodObserverRegistry;

        public Observers(ObserverRegistry<ParserRuleContext> nodeObserverRegistry, ObserverRegistry<Symbol> symbolObserverRegistry, ObserverRegistry<String> methodObserverRegistry) {
            this.nodeObserverRegistry = nodeObserverRegistry;
            this.symbolObserverRegistry = symbolObserverRegistry;
            this.methodObserverRegistry = methodObserverRegistry;
        }
    }

    public BaseListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener, Observers observers) {
        this.analysisContext = analysisContext;
        this.parseTreeListener = parseTreeListener;
        this.nodeObserverRegistry = observers.nodeObserverRegistry;
        this.symbolObserverRegistry = observers.symbolObserverRegistry;
        this.methodObserverRegistry = observers.methodObserverRegistry;
    }

    protected void storeSymbolTable(ParseTree ctx) {
        analysisContext.storeSymbolTable(ctx);
    }

    protected SymbolTable<Symbol> getSymbolTable() {
        return analysisContext.getSymbolTable();
    }

    protected List<Symbol> getSymbols() {
        return analysisContext.getSymbols();
    }

    protected void wrapSymbolTable() {
        analysisContext.wrapSymbolTable();
    }

    protected void unwrapSymbolTable() {
        analysisContext.unwrapSymbolTable();
    }

    protected MethodTable getMethodTable() {
        return analysisContext.getMethodTable();
    }

    protected void addException(ThoriumSemanticException exception) {
        analysisContext.addException(exception);
    }

    protected void registerNodeObserver(ParserRuleContext observer, ParserRuleContext observable) {
        nodeObserverRegistry.registerObserver(observer, observable);
    }

    protected void notifyNodeObservers(ParserRuleContext observable) {
        nodeObserverRegistry.notifyObservers(observable, parseTreeListener);
    }

    protected void registerSymbolObserver(ParserRuleContext observer, Symbol observable) {
        symbolObserverRegistry.registerObserver(observer, observable);
    }

    protected void notifySymbolObservers(Symbol observable) {
        symbolObserverRegistry.notifyObservers(observable, parseTreeListener);
    }

    protected void registerMethodObserver(ParserRuleContext observer, String observable) {
        methodObserverRegistry.registerObserver(observer, observable);
    }

    protected void notifyMethodObservers(String observable) {
        methodObserverRegistry.notifyObservers(observable, parseTreeListener);
    }

    protected void setNodeTypes(ParseTree ctx, Set<Type> types) {
        analysisContext.setNodeTypes(ctx, types);
    }

    protected Set<Type> getNodeTypes(ParseTree ctx) {
        return analysisContext.getNodeTypes(ctx);
    }

    protected Type getNodeType(ParserRuleContext ctx) {
        Set<Type> possibleTypes = getNodeTypes(ctx);

        if (possibleTypes.size() != 1) {
            analysisContext.addException(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            return Types.NULLABLE_VOID;
        }

        return possibleTypes.iterator().next();
    }

    protected void inferNodeTypes(ParserRuleContext parent, ParserRuleContext child) {
        Set<Type> childTypes = getNodeTypes(child);

        analysisContext.setNodeTypes(parent, childTypes);

        if (childTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, parseTreeListener);
        }
    }

    /**
     * Finds the type of the parent node from the child node. Assigns the child node's type to the parent node. If the
     * child node has a Types.*_VOID type, then the parent registers itself as an observer of the child node's type
     * changes.
     *
     * @param parent the node for which we want to determine the type
     * @param child  the node from which we try to find the type
     */
    protected void inferNodeType(ParserRuleContext parent, ParserRuleContext child) {
        Type childType = getNodeType(child);

        analysisContext.setNodeTypes(parent, asSet(childType));

        if (childType == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, parseTreeListener);
        }
    }

    protected void registerVariableOrConstant(ParserRuleContext ctx, Symbol.SymbolKind symbolKind, String name, ThoriumParser.TypeContext typeCtx, ThoriumParser.ExpressionContext expressionCtx) {
        Type symbolType = findSymbolType(ctx, typeCtx, expressionCtx);

        if (symbolKind == Symbol.SymbolKind.CONSTANT && symbolType.isNullable()) {
            analysisContext.addException(InvalidTypeException.invalidType(ctx.getStart(), symbolType.nonNullable(), symbolType.nullable()));
            symbolType = symbolType.nonNullable();
        }

        if (!symbolType.isNullable() && expressionCtx == null) {
            analysisContext.addException(InvalidTypeException.invalidType(ctx.getStart(), symbolType.nullable(), symbolType.nonNullable()));
            symbolType = symbolType.nullable();
        }

        Symbol symbol = registerSymbol(symbolKind, name, symbolType, ctx);

        if (symbol.getDefinedAt() != null && symbol.getDefinedAt() != ctx) {
            analysisContext.addException(InvalidSymbolException.alreadyDefined(ctx.getStart(), name, symbol.getDefinedAt().getStart()));
        } else {
            symbol.setDefinedAt(ctx);
        }

        if (symbol.getType() == Types.NULLABLE_VOID) {
            symbol.setType(symbolType);
        }

        analysisContext.setNodeTypes(ctx, asSet(symbol.getType()));

        if (symbol.getType() != Types.NULLABLE_VOID) {
            nodeObserverRegistry.notifyObservers(ctx, parseTreeListener);
            symbolObserverRegistry.notifyObservers(symbol, parseTreeListener);
        }
    }

    protected static Set<Type> asSet(Type... types) {
        return new HashSet<>(Arrays.asList(types));
    }

    private Type findSymbolType(ParserRuleContext ctx, ThoriumParser.TypeContext typeCtx, ThoriumParser.ExpressionContext expressionCtx) {
        Type symbolType = Types.NULLABLE_VOID;

        if (typeCtx != null) {
            symbolType = getNodeType(typeCtx);
        }

        Type expressionType;
        if (expressionCtx != null) {
            expressionType = getNodeType(expressionCtx);

            if (expressionType != Types.NULLABLE_VOID) {

                if (!Type.isAssignableTo(symbolType, expressionType)) {
                    analysisContext.addException(InvalidTypeException.notCompatible(expressionCtx.getStart(), expressionType, symbolType));
                } else if (symbolType == Types.NULLABLE_VOID) {
                    symbolType = expressionType;
                }
            }

            if (symbolType == Types.NULLABLE_VOID) {
                nodeObserverRegistry.registerObserver(ctx, expressionCtx);
            }
        }

        return symbolType;
    }

    protected Symbol registerSymbol(Symbol.SymbolKind kind, String name, Type type, ParserRuleContext ctx) {
        SymbolTable<Symbol> ctxOriginalScope = analysisContext.getSymbolTable(ctx);

        if (!ctxOriginalScope.isDefinedInCurrentScope(name)) {
            Symbol symbol = Symbol.create(name, kind, type, ctx.getStart());
            analysisContext.addSymbol(symbol);
            ctxOriginalScope.insert(name, symbol);
        }

        return ctxOriginalScope.lookup(name);
    }
}
