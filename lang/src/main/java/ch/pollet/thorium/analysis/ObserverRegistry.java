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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class ObserverRegistry<T> {
    private Map<T, List<ParserRuleContext>> observers = new IdentityHashMap<>();

    public void registerObserver(ParserRuleContext observer, T observable) {
        if (observers.get(observable) == null) {
            resetRegisteredObservers(observable);
        }

        if (!observers.get(observable).contains(observer)) {
            observers.get(observable).add(observer);
        }
    }

    private void resetRegisteredObservers(T observable) {
        observers.put(observable, new LinkedList<>());
    }

    public void notifyObservers(T observable, ParseTreeListener parseTreeListener) {
        ParseTreeWalker walker = new ParseTreeWalker();

        Iterator<ParserRuleContext> it = getRegisteredObservers(observable).iterator();
        while (it.hasNext()) {
            ParserRuleContext observer = it.next();
            it.remove();

            walker.walk(parseTreeListener, observer);
        }
    }

    private List<ParserRuleContext> getRegisteredObservers(T observable) {
        if (observers.get(observable) == null) {
            resetRegisteredObservers(observable);
        }
        return observers.get(observable);
    }
}
