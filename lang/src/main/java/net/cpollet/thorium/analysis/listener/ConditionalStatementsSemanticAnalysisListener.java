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
import net.cpollet.thorium.analysis.exceptions.InvalidTypeException;
import net.cpollet.thorium.antlr.ThoriumParser;
import net.cpollet.thorium.types.Type;
import net.cpollet.thorium.types.Types;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * @author Christophe Pollet
 */
public class ConditionalStatementsSemanticAnalysisListener extends BaseSemanticAnalysisListener {
    public ConditionalStatementsSemanticAnalysisListener(AnalysisContext analysisContext, ParseTreeListener parseTreeListener,
                                                         ObserverRegistry<ParserRuleContext> nodeObserverRegistry,
                                                         ObserverRegistry<Symbol> symbolObserverRegistry) {
        super(analysisContext, parseTreeListener, nodeObserverRegistry, symbolObserverRegistry);
    }

    public void exitUnconditionalStatement(ThoriumParser.UnconditionalStatementContext ctx) {
        findNodeType(ctx, ctx.expression());
    }

    public void exitConditionalIfStatement(ThoriumParser.ConditionalIfStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    private void conditionalOrRepeatedStatement(ParserRuleContext ctx, ThoriumParser.ExpressionContext expressionCtx, ThoriumParser.ExpressionContext conditionCtx) {
        findNodeType(ctx, expressionCtx);

        // conditional statements are always nullable, as we are not sure they will actually by executed and thus that
        // they will return an actual non-null value...
        setTypesOf(ctx, asSet(getNodeType(ctx).nullable()));

        Type type = getNodeType(conditionCtx);

        if (type != Types.BOOLEAN) {
            addException(InvalidTypeException.invalidType(conditionCtx.getStart(), Types.BOOLEAN, type));
        }
    }

    public void exitConditionalUnlessStatement(ThoriumParser.ConditionalUnlessStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    public void exitRepeatedWhileStatement(ThoriumParser.RepeatedWhileStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }

    public void exitRepeatedUntilStatement(ThoriumParser.RepeatedUntilStatementContext ctx) {
        conditionalOrRepeatedStatement(ctx, ctx.expression(0), ctx.expression(1));
    }
}
