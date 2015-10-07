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
import net.cpollet.thorium.analysis.data.symbol.Symbol;
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class ControlStatementsListener extends BaseListener {
    public ControlStatementsListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener, Observers observers) {
        super(analysisContext, parseTreeListener, observers);
    }

    // region Conditionals

    public void enterIfStatement() {
        wrapSymbolTable();
    }

    public void exitIfStatement(ThoriumParser.IfStatementContext ctx) {
        Type conditionType = getNodeType(ctx.expression());

        if (conditionType != Types.BOOLEAN) {
            addException(InvalidTypeException.invalidType(ctx.expression().getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> leftBranchTypes = getNodeTypes(ctx.statements());
        if (leftBranchTypes.contains(Types.NULLABLE_VOID)) {
            registerNodeObserver(ctx, ctx.statements());
        } else {
            notifyNodeObservers(ctx);
        }

        Set<Type> rightBranchTypes = Collections.emptySet();
        if (ctx.elseStatement() != null) {
            rightBranchTypes = getNodeTypes(ctx.elseStatement());
            if (leftBranchTypes.contains(Types.NULLABLE_VOID)) {
                registerNodeObserver(ctx, ctx.elseStatement());
            } else {
                notifyNodeObservers(ctx);
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

        setNodeTypes(ctx, possibleTypes);

        unwrapSymbolTable();
    }

    private static <T> Set<T> intersect(Set<T> left, Set<T> right) {
        if (left.isEmpty() || right.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(left);
        result.retainAll(right);
        return result;
    }

    public void enterElseStatement() {
        wrapSymbolTable();
    }

    public void exitElseStatement(ThoriumParser.ElseStatementContext ctx) {
        if (ctx.statements() != null) {
            inferNodeTypes(ctx, ctx.statements());
        } else if (ctx.ifStatement() != null) {
            inferNodeTypes(ctx, ctx.ifStatement());
        } else {
            throw new IllegalArgumentException();
        }

        unwrapSymbolTable();
    }

    // endregion

    // region Loops

    public void exitForLoopStatement(ThoriumParser.ForLoopStatementContext ctx) {
        exitLoopStatement(ctx, ctx.statements(), ctx.condition);
    }

    public void exitForLoopStatementInitVariableDeclaration(ThoriumParser.ForLoopStatementInitVariableDeclarationContext ctx) {
        registerVariableOrConstant(ctx, Symbol.SymbolKind.VARIABLE, ctx.LCFirstIdentifier().getText(), ctx.type(), ctx.expression());
    }

    public void exitWhileLoopStatement(ThoriumParser.WhileLoopStatementContext ctx) {
        exitLoopStatement(ctx, ctx.statements(), ctx.expression());
    }

    private void exitLoopStatement(ParserRuleContext ctx, ThoriumParser.StatementsContext stmtsCtx, ThoriumParser.ExpressionContext exprCtx) {
        Type conditionType = getNodeType(exprCtx);

        if (conditionType != Types.BOOLEAN) {
            addException(InvalidTypeException.invalidType(exprCtx.getStart(), Types.BOOLEAN, conditionType));
        }

        Set<Type> possibleTypes = getNodeTypes(stmtsCtx);
        if (possibleTypes.contains(Types.NULLABLE_VOID)) {
            registerNodeObserver(ctx, stmtsCtx);
        } else {
            notifyNodeObservers(ctx);
        }

        // we are not sure a loop will be executed once, so types are always nullable...
        possibleTypes = possibleTypes.stream().map(Type::nullable).collect(Collectors.toSet());

        setNodeTypes(ctx, possibleTypes);
    }

    // endregion
}
