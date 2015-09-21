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

import ch.pollet.thorium.ParseTreeSymbolTables;
import ch.pollet.thorium.ThoriumException;
import ch.pollet.thorium.analysis.exceptions.InvalidAssignmentException;
import ch.pollet.thorium.analysis.exceptions.InvalidSymbolException;
import ch.pollet.thorium.analysis.exceptions.InvalidTypeException;
import ch.pollet.thorium.analysis.values.Symbol;
import ch.pollet.thorium.antlr.ThoriumBaseListener;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.data.Method;
import ch.pollet.thorium.execution.SymbolTable;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.types.Types;
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
 * @todo finish tests in semantic_analysis.story
 */
public class SemanticAnalysisListener extends ThoriumBaseListener {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticAnalysisListener.class);

    private final List<String> ruleNames;

    private final ParseTreeTypes types = new ParseTreeTypes();

    private final ParseTreeSymbolTables symbolTables = new ParseTreeSymbolTables();
    private SymbolTable<Symbol> currentSymbolTable;

    private List<Symbol> symbols = new LinkedList<>();

    private final ObserverRegistry<Symbol> symbolObserverRegistry = new ObserverRegistry<>();
    private final ObserverRegistry<ParserRuleContext> nodeObserverRegistry = new ObserverRegistry<>();

    private final List<ThoriumException> exceptions = new ArrayList<>();

    public SemanticAnalysisListener(Parser parser, SymbolTable<Symbol> baseScope) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
        this.currentSymbolTable = baseScope;
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
            return Types.NULLABLE_VOID;
        }

        return possibleTypes.iterator().next();
    }

    private Set<Type> getNodeTypes(ParseTree ctx) {
        return types.get(ctx);
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        if (symbolTables.get(ctx) == null) {
            symbolTables.put(ctx, currentSymbolTable);
        }
    }

    @Override
    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        //noinspection Convert2streamapi
        for (Symbol symbol : symbols) {
            if (symbol.getType() == Types.NULLABLE_VOID) {
                exceptions.add(InvalidTypeException.typeExpected(symbol.getToken()));
            }
        }
    }

    //region Statements

    @Override
    public void enterBlock(ThoriumParser.BlockContext ctx) {
        currentSymbolTable = currentSymbolTable.wrap();
    }

    @Override
    public void exitBlock(ThoriumParser.BlockContext ctx) {
        if (ctx.ifStatement() != null) {
            findNodeTypes(ctx, ctx.ifStatement());
        } else if (ctx.statementsBlock() != null) {
            findNodeTypes(ctx, ctx.statementsBlock());
        } else if (ctx.forLoopStatement() != null) {
            findNodeTypes(ctx, ctx.forLoopStatement());
        } else if (ctx.whileLoopStatement() != null) {
            findNodeTypes(ctx, ctx.whileLoopStatement());
        } else {
            throw new IllegalStateException("Unhandled block type");
        }

        currentSymbolTable = currentSymbolTable.unwrap();
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
        } else if (ctx.variableOrConstantDeclarationStatement() != null) {
            findNodeTypes(ctx, ctx.variableOrConstantDeclarationStatement());
        } else if (";".equals(ctx.getText())) {
            types.put(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void exitVariableDeclarationStatement(ThoriumParser.VariableDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.VARIABLE, ctx.LCFirstIdentifier().getText(), ctx.type(), ctx.expression());

        logContextInformation(ctx);
    }

    private void registerVariableOrConstant(ParserRuleContext ctx, Symbol.SymbolKind symbolKind, String name, ThoriumParser.TypeContext typeCtx, ThoriumParser.ExpressionContext expressionCtx) {
        Type symbolType = findSymbolType(ctx, typeCtx, expressionCtx);

        Symbol symbol = registerSymbol(symbolKind, name, symbolType, ctx);

        if (symbol.getDefinedAt() != null && symbol.getDefinedAt() != ctx) {
            exceptions.add(InvalidSymbolException.alreadyDefined(ctx.getStart(), name, symbol.getDefinedAt().getStart()));
        } else {
            symbol.setDefinedAt(ctx);
        }

        if (symbol.getType() == Types.NULLABLE_VOID) {
            symbol.setType(symbolType);
        }

        types.put(ctx, asSet(symbol.getType()));

        if (symbol.getType() != Types.NULLABLE_VOID) {
            nodeObserverRegistry.notifyObservers(ctx, this);
            symbolObserverRegistry.notifyObservers(symbol, this);
        }
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
                    exceptions.add(InvalidTypeException.notCompatible(expressionCtx.getStart(), expressionType, symbolType));
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

    private Symbol registerSymbol(Symbol.SymbolKind kind, String name, Type type, ParserRuleContext ctx) {
        SymbolTable<Symbol> ctxOriginalScope = symbolTables.get(ctx);

        if (!ctxOriginalScope.isDefinedInCurrentScope(name)) {
            Symbol symbol = Symbol.create(name, kind, type, ctx.getStart());
            symbols.add(symbol);
            ctxOriginalScope.putInCurrentScope(name, symbol);
        }

        return ctxOriginalScope.lookup(name);
    }

    @Override
    public void exitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.CONSTANT, ctx.UCIdentifier().getText(), ctx.type(), ctx.expression());

        currentSymbolTable.lookup(ctx.UCIdentifier().getText()).lock();

        logContextInformation(ctx);
    }

    //endregion

    //region If Statement

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        Type conditionType = getNodeType(ctx.expression());

        if (conditionType != Types.BOOLEAN) {
            exceptions.add(InvalidTypeException.invalidType(ctx.expression().getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> possibleTypes = getNodeTypes(ctx.statements());
        if (possibleTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(ctx, ctx.statements());
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        if (ctx.elseStatement() != null) {
            Set<Type> possibleTypesFromFalseBranch = getNodeTypes(ctx.elseStatement());
            if (possibleTypes.contains(Types.NULLABLE_VOID)) {
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

    //region Loop Statements

    @Override
    public void exitForLoopStatement(ThoriumParser.ForLoopStatementContext ctx) {
        exitLoopStatement(ctx, ctx.statements(), ctx.condition);
    }

    @Override
    public void exitForLoopStatementInitVariableDeclaration(ThoriumParser.ForLoopStatementInitVariableDeclarationContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.VARIABLE, ctx.LCFirstIdentifier().getText(), ctx.type(), ctx.expression());
    }

    @Override
    public void exitWhileLoopStatement(ThoriumParser.WhileLoopStatementContext ctx) {
        exitLoopStatement(ctx, ctx.statements(), ctx.expression());
    }

    private void exitLoopStatement(ParserRuleContext ctx, ThoriumParser.StatementsContext stmtsCtx, ThoriumParser.ExpressionContext exprCtx) {
        Type conditionType = getNodeType(exprCtx);

        if (conditionType != Types.BOOLEAN) {
            exceptions.add(InvalidTypeException.invalidType(exprCtx.getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> possibleTypes = getNodeTypes(stmtsCtx);
        if (possibleTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(ctx, stmtsCtx);
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        types.put(ctx, possibleTypes);

        logContextInformation(ctx);
    }

    //endregion

    //region Mono-valued statements

    @Override
    public void exitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    @Override
    public void exitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    private void conditionalOrRepeatedStatement(ParserRuleContext ctx, ThoriumParser.ExpressionContext expressionCtx, ThoriumParser.ExpressionContext conditionCtx) {
        findNodeType(ctx, expressionCtx);

        Type type = getNodeType(conditionCtx);

        if (type != Types.BOOLEAN) {
            exceptions.add(InvalidTypeException.invalidType(conditionCtx.getStart(), Types.BOOLEAN, type));
        }
    }

    @Override
    public void exitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    @Override
    public void exitRepeatedWhileStatement(ThoriumParser.RepeatedWhileStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    @Override
    public void exitRepeatedUntilStatement(ThoriumParser.RepeatedUntilStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    //endregion

    //region Expressions

    @Override
    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        findNodeType(ctx, ctx.literal());
    }

    @Override
    public void exitNotExpression(ThoriumParser.NotExpressionContext ctx) {
        Type type = getNodeType(ctx.expression());

        if (type == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(ctx, ctx.expression());
            types.put(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), ctx.op.getText(), type);
            types.put(ctx, asSet(resultType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    private void exitBinaryOperator(String operator, ThoriumParser.ExpressionContext leftExpr, ThoriumParser.ExpressionContext rightExpr, ParserRuleContext ctx) {
        Type leftType = getNodeType(leftExpr);
        Type rightType = getNodeType(rightExpr);

        if (leftType == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(ctx, leftExpr);
        }
        if (rightType == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(ctx, rightExpr);
        }

        if (leftType == Types.NULLABLE_VOID || rightType == Types.NULLABLE_VOID) {
            types.put(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), operator, leftType, rightType);
            types.put(ctx, asSet(resultType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    private Type inferMethodType(Token token, String methodName, Type leftType, Type... parametersTypes) {
        if (!leftType.isMethodDefined(methodName, parametersTypes)) {
            exceptions.add(InvalidSymbolException.methodNotFound(token, methodName, leftType, parametersTypes));
            return Types.NULLABLE_VOID;
        }

        Method method = leftType.lookupMethod(methodName, parametersTypes);

        return method.getMethodSignature().getReturnType();
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    @Override
    public void exitOrderComparisonExpression(ThoriumParser.OrderComparisonExpressionContext ctx) {
        exitBinaryOperator(ctx.op.getText(), ctx.expression(0), ctx.expression(1), ctx);
    }

    @Override
    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        Type leftType = getNodeType(ctx.identifier());
        Type rightType = getNodeType(ctx.expression());

        Symbol symbol = currentSymbolTable.lookup(ctx.identifier().getText());

        if (!symbol.isWritable()) {
            exceptions.add(InvalidAssignmentException.build(ctx.start));
            types.put(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        symbol.lock();

        if (rightType == Types.NULLABLE_VOID) {
            types.put(ctx, asSet(Types.NULLABLE_VOID));
            nodeObserverRegistry.registerObserver(ctx, ctx.expression());
            logContextInformation(ctx);
            return;
        }

        if (leftType == Types.NULLABLE_VOID) {
            symbol.setType(rightType.nullable());
            types.put(ctx, asSet(rightType.nullable()));
            symbolObserverRegistry.notifyObservers(symbol, this);
            nodeObserverRegistry.notifyObservers(ctx, this);
        } else if (rightType.isAssignableTo(leftType)) {
            types.put(ctx, asSet(leftType));
            symbolObserverRegistry.notifyObservers(symbol, this);
            nodeObserverRegistry.notifyObservers(ctx, this);
        } else {
            exceptions.add(InvalidTypeException.notCompatible(ctx.getStart(), rightType, leftType));
            types.put(ctx, asSet(Types.NULLABLE_VOID));
        }

    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = types.get(ctx.block());

        if (possibleTypes.size() > 1) {
            exceptions.add(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            types.put(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        findNodeType(ctx, ctx.block());
    }

    //endregion

    //region Values

    @Override
    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        types.put(ctx, asSet(Types.BOOLEAN));
        logContextInformation(ctx);
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        types.put(ctx, asSet(Types.INTEGER));
        logContextInformation(ctx);
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        types.put(ctx, asSet(Types.FLOAT));
        logContextInformation(ctx);
    }

    @Override
    public void exitIdentifierLiteral(ThoriumParser.IdentifierLiteralContext ctx) {
        findNodeType(ctx, ctx.identifier());
    }

    @Override
    public void exitVariableName(ThoriumParser.VariableNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.LCFirstIdentifier().getText(), Symbol.SymbolKind.VARIABLE);
    }

    private void exitVariableOrConstantName(ParserRuleContext ctx, String name, Symbol.SymbolKind kind) {
        if (!currentSymbolTable.isDefined(name)) {
            exceptions.add(InvalidSymbolException.identifierNotFound(ctx.getStart(), name));
            registerSymbol(kind, name, Types.NULLABLE_VOID, ctx);
        }

        Symbol symbol = currentSymbolTable.lookup(name);
        types.put(ctx, asSet(symbol.getType()));

        if (symbol.getType() == Types.NULLABLE_VOID) {
            symbolObserverRegistry.registerObserver(ctx, symbol);
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    @Override
    public void exitConstantName(ThoriumParser.ConstantNameContext ctx) {
        exitVariableOrConstantName(ctx, ctx.UCIdentifier().getText(), Symbol.SymbolKind.CONSTANT);
    }

    //endregion

    @Override
    public void exitType(ThoriumParser.TypeContext ctx) {
        Type.Nullable nullable = ctx.nullable != null ? Type.Nullable.YES : Type.Nullable.NO;

        switch (ctx.UCFirstIdentifier().getText()) {
            case "Integer":
                types.put(ctx, asSet(Types.get(Types.INTEGER, nullable)));
                break;
            case "Float":
                types.put(ctx, asSet(Types.get(Types.FLOAT, nullable)));
                break;
            case "Boolean":
                types.put(ctx, asSet(Types.get(Types.BOOLEAN, nullable)));
                break;
            default:
                throw new IllegalStateException("Invalid type");
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
    private void findNodeType(ParserRuleContext parent, ParserRuleContext child) {
        Type childType = getNodeType(child);

        types.put(parent, asSet(childType));

        if (childType == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, this);
        }

        logContextInformation(parent);
    }

    private void findNodeTypes(ParserRuleContext parent, ParserRuleContext child) {
        Set<Type> childTypes = getNodeTypes(child);

        types.put(parent, childTypes);

        if (childTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, this);
        }

        logContextInformation(parent);
    }

    private static Set<Type> asSet(Type... types) {
        return new HashSet<>(Arrays.asList(types));
    }

    private void logContextInformation(ParserRuleContext ctx) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        int i = 2;
        String methodName = stackTraceElements[i].getMethodName();

        while (!methodName.startsWith("exit") && !methodName.startsWith("enter")) {
            methodName = stackTraceElements[i++].getMethodName();
        }

        LOG.debug("-> [" + methodName + "] " + ctx.toString(ruleNames) + " " + ctx.toStringTree(ruleNames) + ": " + types.get(ctx));
    }
}
