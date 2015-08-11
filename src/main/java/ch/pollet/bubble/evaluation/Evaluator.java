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

package ch.pollet.bubble.evaluation;

import ch.pollet.bubble.antlr.BubbleBaseListener;
import ch.pollet.bubble.antlr.BubbleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * @author Christophe Pollet
 */
public class Evaluator extends BubbleBaseListener {
    private EvaluationContext evaluationContext;

    public Evaluator(EvaluationContext evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public void exitAdditionExpression(BubbleParser.AdditionExpressionContext ctx) {
        Integer right = (Integer) evaluationContext.popStack();
        Integer left = (Integer) evaluationContext.popStack();

        evaluationContext.pushStack(right + left);
    }

    @Override
    public void exitMultiplicationExpression(BubbleParser.MultiplicationExpressionContext ctx) {
        Integer right = (Integer) evaluationContext.popStack();
        Integer left = (Integer) evaluationContext.popStack();

        evaluationContext.pushStack(right * left);
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() == BubbleParser.IntegerLiteral) {
            evaluationContext.pushStack(Integer.valueOf(node.getText()));
        }
    }
}
