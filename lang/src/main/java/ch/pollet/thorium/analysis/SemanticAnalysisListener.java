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

import ch.pollet.thorium.ThoriumException;
import ch.pollet.thorium.analysis.exceptions.InvalidAssignmentException;
import ch.pollet.thorium.analysis.exceptions.InvalidTypeException;
import ch.pollet.thorium.analysis.exceptions.MethodNotFoundException;
import ch.pollet.thorium.analysis.values.Symbol;
import ch.pollet.thorium.antlr.ThoriumBaseListener;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.evaluation.Method;
import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.evaluation.SymbolTable;
import ch.pollet.thorium.types.Type;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 * @fixme implement scopes support
 * @fixme check symbol does not already exist
 */
public class SemanticAnalysisListener extends ThoriumBaseListener {
    private final static Logger LOG = LoggerFactory.getLogger(SemanticAnalysisListener.class);

    private final List<String> ruleNames;

    private final ParseTreeTypes types = new ParseTreeTypes();

    private SymbolTable<Symbol> currentScope;
    private List<Symbol> symbols = new LinkedList<>();

    private final ObserverRegistry<Symbol> symbolObserverRegistry = new ObserverRegistry<>();
    private final ObserverRegistry<ParserRuleContext> nodeObserverRegistry = new ObserverRegistry<>();

    private final List<ThoriumException> exceptions = new ArrayList<>();

    public SemanticAnalysisListener(Parser parser, SymbolTable<Symbol> baseScope) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
        this.currentScope = baseScope;
    }

    public List<ThoriumException> getExceptions() {
        return exceptions;
    }

    public ParseTreeProperty<Type> getTypes() {
        return types.reduce();
    }

    private Type getNodeType(ParserRuleContext ctx) {
        Set<Type> possibleTypes = types.get(ctx);
        if (possibleTypes.size() != 1) {
            exceptions.add(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            return Type.VOID;
        }

        return possibleTypes.iterator().next();
    }

    private Set<Type> getNodeTypes(ParseTree ctx) {
        return types.get(ctx);
    }

    @Override
    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        for (Symbol symbol : symbols) {
            if (symbol.getType() == Type.VOID) {
                exceptions.add(InvalidTypeException.typeExpected(symbol.getToken()));
            }
        }
    }

    //region Statements

    @Override
    public void exitBlock(ThoriumParser.BlockContext ctx) {
        if (ctx.ifStatement() != null) {
            findNodeTypes(ctx, ctx.ifStatement());
        } else if (ctx.statementsBlock() != null) {
            findNodeTypes(ctx, ctx.statementsBlock());
        }
    }

    @Override
    public void exitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        findNodeTypes(ctx, ctx.statements());
    }

    @Override
    public void exitStatements(ThoriumParser.StatementsContext ctx) {
        findNodeTypes(ctx, ctx.statement(ctx.statement().size() - 1));
    }

    @Override
    public void exitStatement(ThoriumParser.StatementContext ctx) {
        if (ctx.block() != null) {
            findNodeTypes(ctx, ctx.block());
        } else if (ctx.expressionStatement() != null) {
            findNodeTypes(ctx, ctx.expressionStatement());
        } else if (ctx.getText().equals(";")) {
            types.put(ctx, asSet(Type.VOID));
        } else {
            throw new IllegalStateException();
        }
    }

    //endregion

    //region If Statement

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        Type conditionType = getNodeType(ctx.expression());
        if (conditionType != Type.BOOLEAN) {
            exceptions.add(InvalidTypeException.invalidType(ctx.expression().getStart(), Type.BOOLEAN, conditionType));
        }

        Set<Type> possibleTypes = getNodeTypes(ctx.statements());
        if (possibleTypes.contains(Type.VOID)) {
            nodeObserverRegistry.registerObserver(ctx, ctx.statements());
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        if (ctx.elseStatement() != null) {
            Set<Type> possibleTypesFromFalseBranch = getNodeTypes(ctx.elseStatement());
            if (possibleTypes.contains(Type.VOID)) {
                nodeObserverRegistry.registerObserver(ctx, ctx.elseStatement());
            } else {
                nodeObserverRegistry.notifyObservers(ctx, this);
            }

            possibleTypes.addAll(possibleTypesFromFalseBranch);
        }

        types.put(ctx, possibleTypes);

        logContextInformation(ctx);
    }

    @Override
    public void exitElseStatement(ThoriumParser.ElseStatementContext ctx) {
        if (ctx.statements() != null) {
            findNodeTypes(ctx, ctx.statements());
        } else if (ctx.ifStatement() != null) {
            findNodeTypes(ctx, ctx.ifStatement());
        } else {
            throw new IllegalArgumentException();
        }
    }

    //endregion

    //region Mono-values statements

    @Override
    public void exitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    @Override
    public void exitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        findNodeType(ctx, ctx.expression(0));
    }

    @Override
    public void exitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        findNodeType(ctx, ctx.expression(0));
    }

    //endregion

    //region Expressions

    @Override
    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        findNodeType(ctx, ctx.literal());
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        Type leftType = getNodeType(ctx.expression(0));
        Type rightType = getNodeType(ctx.expression(1));

        if (leftType == Type.VOID) {
            nodeObserverRegistry.registerObserver(ctx, ctx.expression(0));
        }
        if (rightType == Type.VOID) {
            nodeObserverRegistry.registerObserver(ctx, ctx.expression(1));
        }

        if (leftType == Type.VOID || rightType == Type.VOID) {
            types.put(ctx, asSet(Type.VOID));
        } else {
            Type resultType = inferMethodType(ctx.start, "*", leftType, rightType);
            types.put(ctx, asSet(resultType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    private Type inferMethodType(Token token, String methodName, Type leftType, Type... parametersTypes) {
        Method method = leftType.lookupMethod(new MethodMatcher("*", parametersTypes));

        if (method == null) {
            exceptions.add(MethodNotFoundException.build(token, methodName, leftType, parametersTypes));
            return Type.VOID;
        }

        return method.getType();
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        Type leftType = getNodeType(ctx.expression(0));
        Type rightType = getNodeType(ctx.expression(1));

        if (leftType == Type.VOID) {
            nodeObserverRegistry.registerObserver(ctx, ctx.expression(0));
        }
        if (rightType == Type.VOID) {
            nodeObserverRegistry.registerObserver(ctx, ctx.expression(1));
        }

        if (leftType == Type.VOID || rightType == Type.VOID) {
            types.put(ctx, asSet(Type.VOID));
        } else {
            Type resultType = inferMethodType(ctx.start, "+", leftType, rightType);
            types.put(ctx, asSet(resultType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    @Override
    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Type leftType = getNodeType(ctx.identifier());
        Type rightType = getNodeType(ctx.expression());

        Symbol symbol = currentScope.get(ctx.identifier().getText());

        if (!symbol.isWritable()) {
            exceptions.add(InvalidAssignmentException.build(ctx.start));
            types.put(ctx, asSet(Type.VOID));
            return;
        }

        symbol.lock();

        if (rightType != Type.VOID) {
            if (leftType == Type.VOID) {
                symbol.setType(rightType);
                symbolObserverRegistry.notifyObservers(symbol, this);
            } else if (!Type.isAssignableFrom(leftType, rightType)) {
                exceptions.add(InvalidTypeException.notCompatible(ctx.getStart(), rightType, leftType));
                types.put(ctx, asSet(Type.VOID));
                return;
            }

            types.put(ctx, asSet(rightType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        } else {
            types.put(ctx, asSet(Type.VOID));
            nodeObserverRegistry.registerObserver(ctx, ctx.expression());
        }

        logContextInformation(ctx);
    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = types.get(ctx.block());

        if (possibleTypes.size() > 1) {
            exceptions.add(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            types.put(ctx, asSet(Type.VOID));
            return;
        }

        findNodeType(ctx, ctx.block());
    }

    //endregion

    //region Values

    @Override
    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        types.put(ctx, asSet(Type.BOOLEAN));
        logContextInformation(ctx);
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        types.put(ctx, asSet(Type.INTEGER));
        logContextInformation(ctx);
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        types.put(ctx, asSet(Type.FLOAT));
        logContextInformation(ctx);
    }

    @Override
    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        findNodeType(ctx, ctx.identifier());
    }

    @Override
    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        registerSymbol(Symbol.SymbolType.VARIABLE, ctx);
    }

    @Override
    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        registerSymbol(Symbol.SymbolType.CONSTANT, ctx);
    }

    private void registerSymbol(Symbol.SymbolType type, ParserRuleContext ctx) {
        String name = ctx.getText();

        if (!currentScope.isDefined(name)) {
            Symbol symbol = Symbol.create(type, ctx.getStart());
            symbols.add(symbol);
            currentScope.put(name, symbol);
        }

        Symbol symbol = currentScope.get(name);
        types.put(ctx, asSet(symbol.getType()));

        if (symbol.getType() == Type.VOID) {
            symbolObserverRegistry.registerObserver(ctx, symbol);
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    //endregion

    /**
     * Finds the type of the parent node from the child node. Assigns the child node's type to the parent node. If the
     * child node has a Type.VOID type, then the parent registers itself as an observer of the child node's type
     * changes.
     *
     * @param parent the node for which we want to determine the type
     * @param child  the node from which we try to find the type
     */
    private void findNodeType(ParserRuleContext parent, ParserRuleContext child) {
        Type childType = getNodeType(child);

        types.put(parent, asSet(childType));

        if (childType == Type.VOID) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, this);
        }

        logContextInformation(parent);
    }

    private void findNodeTypes(ParserRuleContext parent, ParserRuleContext child) {
        Set<Type> childTypes = getNodeTypes(child);

        types.put(parent, childTypes);

        if (childTypes.contains(Type.VOID)) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, this);
        }

        logContextInformation(parent);
    }

    private Set<Type> asSet(Type... types) {
        return new HashSet<>(Arrays.asList(types));
    }

    private void logContextInformation(ParserRuleContext ctx) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int i = 2;
        String methodName = stackTraceElements[i].getMethodName();

        while (!methodName.startsWith("exit") && !methodName.startsWith("enter")) {
            methodName = stackTraceElements[i++].getMethodName();
        }

        // LOG.info("-> [" + methodName + "] " + ctx.toString(ruleNames) + " " + ctx.toStringTree(ruleNames) + ": " + types.get(ctx));
    }
}