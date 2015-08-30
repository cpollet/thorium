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

import ch.pollet.thorium.analysis.exceptions.InvalidTypeException;
import ch.pollet.thorium.antlr.ThoriumBaseListener;
import ch.pollet.thorium.antlr.ThoriumParser;
import ch.pollet.thorium.evaluation.MethodMatcher;
import ch.pollet.thorium.evaluation.SymbolTable;
import ch.pollet.thorium.types.Type;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Christophe Pollet
 */
public class TypeAnalysisListener extends ThoriumBaseListener {
    private final static Logger LOG = LoggerFactory.getLogger(TypeAnalysisListener.class);

    private final List<String> ruleNames;
    private ParseTreeProperty<SymbolTable> symbolTables = new ParseTreeProperty<>();
    private ParseTreeProperty<Set<Type>> types = new ParseTreeProperty<>();
    private SymbolTable currentScope;

    public TypeAnalysisListener(Parser parser) {
        this.ruleNames = Arrays.asList(parser.getRuleNames());
    }

    public Set<Type> getNodeTypes(ParseTree ctx) {
        return types.get(ctx);
    }

    @Override
    public void enterCompilationUnit(ThoriumParser.CompilationUnitContext ctx) {
        currentScope = new SymbolTable();
    }

    //region Statements

    @Override
    public void exitBlock(ThoriumParser.BlockContext ctx) {
        types.put(ctx, types.get(ctx.getChild(0)));
        logContextInformation(ctx);
    }

    @Override
    public void exitStatementsBlock(ThoriumParser.StatementsBlockContext ctx) {
        types.put(ctx, types.get(ctx.statements()));
        logContextInformation(ctx);
    }

    @Override
    public void exitStatements(ThoriumParser.StatementsContext ctx) {
        types.put(ctx, types.get(ctx.getChild(ctx.getChildCount() - 1)));
        logContextInformation(ctx);
    }

    @Override
    public void exitStatement(ThoriumParser.StatementContext ctx) {
        types.put(ctx, types.get(ctx.getChild(0)));
        logContextInformation(ctx);
    }

    @Override
    public void exitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        types.put(ctx, types.get(ctx.expression()));
        logContextInformation(ctx);
    }

    @Override
    public void exitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        types.put(ctx, types.get(ctx.expression(0)));
        logContextInformation(ctx);
    }

    @Override
    public void exitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        types.put(ctx, types.get(ctx.expression(0)));
        logContextInformation(ctx);
    }

    //endregion

    //region If Statement

    @Override
    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        Type conditionType = types.get(ctx.expression()).iterator().next();
        if (conditionType != Type.BOOLEAN) {
            throw InvalidTypeException.invalidType(ctx.expression().getStart(), Type.BOOLEAN, conditionType);
        }

        Set<Type> possibleTypes = new HashSet<>();

        possibleTypes.addAll(types.get(ctx.statements()));
        if (ctx.elseStatement() != null) {
            possibleTypes.addAll(types.get(ctx.elseStatement()));
        }

        types.put(ctx, possibleTypes);

        logContextInformation(ctx);
    }

    @Override
    public void exitElseStatement(ThoriumParser.ElseStatementContext ctx) {
        if (ctx.statements() != null) {
            types.put(ctx, types.get(ctx.statements()));
        } else if (ctx.ifStatement() != null) {
            types.put(ctx, types.get(ctx.ifStatement()));
        } else {
            throw new IllegalArgumentException();
        }
        logContextInformation(ctx);
    }

    //endregion

    //region Expressions

    @Override
    public void exitLiteralExpression(ThoriumParser.LiteralExpressionContext ctx) {
        types.put(ctx, types.get(ctx.literal()));
        logContextInformation(ctx);
    }

    @Override
    public void exitMultiplicationExpression(ThoriumParser.MultiplicationExpressionContext ctx) {
        Type leftType = types.get(ctx.expression(0)).iterator().next();
        Type rightType = types.get(ctx.expression(1)).iterator().next();
        Type resultType = leftType.lookupMethod(new MethodMatcher("*", rightType)).getType();
        types.put(ctx, asSet(resultType));
        logContextInformation(ctx);
    }

    @Override
    public void exitAdditionExpression(ThoriumParser.AdditionExpressionContext ctx) {
        Type leftType = types.get(ctx.expression(0)).iterator().next();
        Type rightType = types.get(ctx.expression(1)).iterator().next();
        Type resultType = leftType.lookupMethod(new MethodMatcher("+", rightType)).getType();
        types.put(ctx, asSet(resultType));
        logContextInformation(ctx);
    }

    @Override
    public void exitParenthesisExpression(ThoriumParser.ParenthesisExpressionContext ctx) {
        types.put(ctx, types.get(ctx.expression()));
        logContextInformation(ctx);
    }

    @Override
    public void exitAssignmentExpression(ThoriumParser.AssignmentExpressionContext ctx) {
        // TODO SEM check type on the left
        types.put(ctx, types.get(ctx.expression()));
        logContextInformation(ctx);
    }

    @Override
    public void exitBlockExpression(ThoriumParser.BlockExpressionContext ctx) {
        Set<Type> possibleTypes = types.get(ctx.block());

        if (possibleTypes.size() > 1) {
            throw InvalidTypeException.ambiguousType(ctx.getStart(), possibleTypes);
        }

        types.put(ctx, possibleTypes);
        logContextInformation(ctx);
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
        // TODO
    }

    //endregion

    private Set<Type> asSet(Type... types) {
        return new HashSet<>(Arrays.asList(types));
    }

    private void logContextInformation(ParserRuleContext ctx) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[2].getMethodName();
        LOG.info("-> [" + methodName + "] " + ctx.toString(ruleNames) + " " + ctx.toStringTree(ruleNames) + ": " + types.get(ctx));
    }
}
