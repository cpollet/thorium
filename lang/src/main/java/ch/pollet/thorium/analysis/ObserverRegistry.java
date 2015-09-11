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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Christophe Pollet
 */
public class ObserverRegistry<T> {
    private final static Logger LOG = LoggerFactory.getLogger(ObserverRegistry.class);

    private final Map<T, List<ParserRuleContext>> observers = new IdentityHashMap<>();

    public void registerObserver(ParserRuleContext observer, T observable) {
        log("Register", observer, observable);

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

            log("Notify", observer, observable);

            walker.walk(parseTreeListener, observer);
        }
    }

    private List<ParserRuleContext> getRegisteredObservers(T observable) {
        if (observers.get(observable) == null) {
            resetRegisteredObservers(observable);
        }
        return observers.get(observable);
    }

    public List<ParserRuleContext> getUnresolvedObservers() {
        return observers.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private void log(String prefix, ParserRuleContext observer, T observable) {
        // LOG.info(prefix + " " + observer.getClass().getSimpleName() + observer.toString() + "@" + System.identityHashCode(observer) + ": " + observer.getText() + " for " + observable.toString());
    }

}
