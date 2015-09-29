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

import net.cpollet.thorium.analysis.exceptions.InvalidAssignmentException;
import net.cpollet.thorium.analysis.exceptions.InvalidSymbolException;
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.antlr.ThoriumBaseListener;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.data.symbol.SymbolTable;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class SemanticAnalysisListener extends ThoriumBaseListener {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticAnalysisListener.class);

    private final List<String> ruleNames;

    private final AnalysisContext analysisContext;

    private final ObserverRegistry<Symbol> symbolObserverRegistry = new ObserverRegistry<>();
    private final ObserverRegistry<ParserRuleContext> nodeObserverRegistry = new ObserverRegistry<>();

    public SemanticAnalysisListener(Parser parser, AnalysisContext analysisContext) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
        this.analysisContext = analysisContext;
    }

    public AnalysisResult getResult() {
        return new AnalysisResult(analysisContext.getTypesOfAllNodes(), analysisContext.getExceptions());
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        analysisContext.storeSymbolTable(ctx);
    }

    @Override
    public void exitCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        //noinspection Convert2streamapi
        for (Symbol symbol : analysisContext.getSymbols()) {
            if (symbol.getType() == Types.NULLABLE_VOID) {
                analysisContext.addException(InvalidTypeException.typeExpected(symbol.getToken()));
            }
        }
    }

    //region Statements

    @Override
    public void enterBlock(ThoriumParser.BlockContext ctx) {
        analysisContext.wrapSymbolTable();
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

        analysisContext.unwrapSymbolTable();
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
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
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

        analysisContext.setTypesOf(ctx, asSet(symbol.getType()));

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

    private Symbol registerSymbol(Symbol.SymbolKind kind, String name, Type type, ParserRuleContext ctx) {
        SymbolTable<Symbol> ctxOriginalScope = analysisContext.getSymbolTable(ctx);

        if (!ctxOriginalScope.isDefinedInCurrentScope(name)) {
            Symbol symbol = Symbol.create(name, kind, type, ctx.getStart());
            analysisContext.addSymbol(symbol);
            ctxOriginalScope.insert(name, symbol);
        }

        return ctxOriginalScope.lookup(name);
    }

    @Override
    public void exitConstantDeclarationStatement(ThoriumParser.ConstantDeclarationStatementContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.CONSTANT, ctx.UCIdentifier().getText(), ctx.type(), ctx.expression());

        analysisContext.getSymbolTable().lookup(ctx.UCIdentifier().getText()).lock();

        logContextInformation(ctx);
    }

    //endregion

    //region If Statement

    @Override
    public void enterIfStatement(ThoriumParser.IfStatementContext ctx) {
        analysisContext.wrapSymbolTable();
    }

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        Type conditionType = getNodeType(ctx.expression());

        if (conditionType != Types.BOOLEAN) {
            analysisContext.addException(InvalidTypeException.invalidType(ctx.expression().getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> leftBranchTypes = getNodeTypes(ctx.statements());
        if (leftBranchTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(ctx, ctx.statements());
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        Set<Type> rightBranchTypes = Collections.emptySet();
        if (ctx.elseStatement() != null) {
            rightBranchTypes = getNodeTypes(ctx.elseStatement());
            if (leftBranchTypes.contains(Types.NULLABLE_VOID)) {
                nodeObserverRegistry.registerObserver(ctx, ctx.elseStatement());
            } else {
                nodeObserverRegistry.notifyObservers(ctx, this);
            }
        }

        // Compute the intersection and remove from each branch the common types
        Set<Type> bothBranchTypes = intersect(leftBranchTypes, rightBranchTypes);
        leftBranchTypes.removeAll(bothBranchTypes);
        rightBranchTypes.removeAll(bothBranchTypes);

        // Types appearing on left or right branches are nullable, as we are unsure about the branch's execution
        leftBranchTypes = leftBranchTypes.stream().map(Type::nullable).collect(Collectors.toSet());
        rightBranchTypes = rightBranchTypes.stream().map(Type::nullable).collect(Collectors.toSet());

        Set<Type> possibleTypes = new HashSet<>(bothBranchTypes.size() + leftBranchTypes.size() + rightBranchTypes.size());
        possibleTypes.addAll(leftBranchTypes);
        possibleTypes.addAll(rightBranchTypes);
        possibleTypes.addAll(bothBranchTypes);

        analysisContext.setTypesOf(ctx, possibleTypes);

        analysisContext.unwrapSymbolTable();

        logContextInformation(ctx);
    }

    private static <T> Set<T> intersect(Set<T> left, Set<T> right) {
        if (left.isEmpty() || right.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(left);
        result.retainAll(right);
        return result;
    }

    @Override
    public void enterElseStatement(ThoriumParser.ElseStatementContext ctx) {
        analysisContext.wrapSymbolTable();
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

        analysisContext.unwrapSymbolTable();
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
            analysisContext.addException(InvalidTypeException.invalidType(exprCtx.getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> possibleTypes = getNodeTypes(stmtsCtx);
        if (possibleTypes.contains(Types.NULLABLE_VOID)) {
            nodeObserverRegistry.registerObserver(ctx, stmtsCtx);
        } else {
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        // we are not sure a loop will be executed once, so types are always nullable...
        possibleTypes = possibleTypes.stream().map(Type::nullable).collect(Collectors.toSet());

        analysisContext.setTypesOf(ctx, possibleTypes);

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

        // conditional statements are always nullable, as we are not sure they will actually by executed and thus that
        // they will return an actual non-null value...
        analysisContext.setTypesOf(ctx, asSet(getNodeType(ctx).nullable()));

        Type type = getNodeType(conditionCtx);

        if (type != Types.BOOLEAN) {
            analysisContext.addException(InvalidTypeException.invalidType(conditionCtx.getStart(), Types.BOOLEAN, type));
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
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), ctx.op.getText(), type);
            analysisContext.setTypesOf(ctx, asSet(resultType));
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
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        } else {
            Type resultType = inferMethodType(ctx.getStart(), operator, leftType, rightType);
            analysisContext.setTypesOf(ctx, asSet(resultType));
            nodeObserverRegistry.notifyObservers(ctx, this);
        }

        logContextInformation(ctx);
    }

    private Type inferMethodType(Token token, String methodName, Type leftType, Type... parameterTypes) {
        List<Type> parameterTypesList = Arrays.asList(parameterTypes);

        if (!leftType.isMethodDefined(methodName, parameterTypesList)) {
            analysisContext.addException(InvalidSymbolException.methodNotFound(token, methodName, leftType, parameterTypesList));
            return Types.NULLABLE_VOID;
        }

        Method method = leftType.lookupMethod(methodName, parameterTypesList);

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

        Symbol symbol = analysisContext.getSymbolTable().lookup(ctx.identifier().getText());

        if (!symbol.isWritable()) {
            analysisContext.addException(InvalidAssignmentException.build(ctx.start));
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        symbol.lock();

        if (rightType == Types.NULLABLE_VOID) {
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            nodeObserverRegistry.registerObserver(ctx, ctx.expression());
            logContextInformation(ctx);
            return;
        }

        if (leftType == Types.NULLABLE_VOID) {
            symbol.setType(rightType.nullable());
            analysisContext.setTypesOf(ctx, asSet(rightType.nullable()));
            symbolObserverRegistry.notifyObservers(symbol, this);
            nodeObserverRegistry.notifyObservers(ctx, this);
        } else if (rightType.isAssignableTo(leftType)) {
            analysisContext.setTypesOf(ctx, asSet(leftType));
            symbolObserverRegistry.notifyObservers(symbol, this);
            nodeObserverRegistry.notifyObservers(ctx, this);
        } else {
            analysisContext.addException(InvalidTypeException.notCompatible(ctx.getStart(), rightType, leftType));
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
        }

    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = analysisContext.getTypesOf(ctx.block());

        if (possibleTypes.size() > 1) {
            analysisContext.addException(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            analysisContext.setTypesOf(ctx, asSet(Types.NULLABLE_VOID));
            return;
        }

        findNodeType(ctx, ctx.block());
    }

    //endregion

    //region Values

    @Override
    public void exitBooleanLiteral(ThoriumParser.BooleanLiteralContext ctx) {
        analysisContext.setTypesOf(ctx, asSet(Types.BOOLEAN));
        logContextInformation(ctx);
    }

    @Override
    public void exitIntegerLiteral(ThoriumParser.IntegerLiteralContext ctx) {
        analysisContext.setTypesOf(ctx, asSet(Types.INTEGER));
        logContextInformation(ctx);
    }

    @Override
    public void exitFloatLiteral(ThoriumParser.FloatLiteralContext ctx) {
        analysisContext.setTypesOf(ctx, asSet(Types.FLOAT));
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
        if (!analysisContext.getSymbolTable().isDefined(name)) {
            analysisContext.addException(InvalidSymbolException.identifierNotFound(ctx.getStart(), name));
            registerSymbol(kind, name, Types.NULLABLE_VOID, ctx);
        }

        Symbol symbol = analysisContext.getSymbolTable().lookup(name);
        analysisContext.setTypesOf(ctx, asSet(symbol.getType()));

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
                analysisContext.setTypesOf(ctx, asSet(Types.get(Types.INTEGER, nullable)));
                break;
            case "Float":
                analysisContext.setTypesOf(ctx, asSet(Types.get(Types.FLOAT, nullable)));
                break;
            case "Boolean":
                analysisContext.setTypesOf(ctx, asSet(Types.get(Types.BOOLEAN, nullable)));
                break;
            default:
                throw new IllegalStateException("Invalid type");
        }
    }

    private Type getNodeType(ParserRuleContext ctx) {
        Set<Type> possibleTypes = analysisContext.getTypesOf(ctx);
        if (possibleTypes.size() != 1) {
            analysisContext.addException(InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes));
            return Types.NULLABLE_VOID;
        }

        return possibleTypes.iterator().next();
    }

    private Set<Type> getNodeTypes(ParseTree ctx) {
        return analysisContext.getTypesOf(ctx);
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

        analysisContext.setTypesOf(parent, asSet(childType));

        if (childType == Types.NULLABLE_VOID) {
            nodeObserverRegistry.registerObserver(parent, child);
        } else {
            nodeObserverRegistry.notifyObservers(parent, this);
        }

        logContextInformation(parent);
    }

    private void findNodeTypes(ParserRuleContext parent, ParserRuleContext child) {
        Set<Type> childTypes = getNodeTypes(child);

        analysisContext.setTypesOf(parent, childTypes);

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

        LOG.debug("-> [" + methodName + "] " + ctx.toString(ruleNames) + " " + ctx.toStringTree(ruleNames) + ": " + analysisContext.getTypesOf(ctx));
    }
}
