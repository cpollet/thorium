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

package ch.pollet.thorium;

import ch.pollet.thorium.analysis.MethodSignature;
import ch.pollet.thorium.analysis.MethodSignatureBuilder;
import ch.pollet.thorium.execution.Operator;
import ch.pollet.thorium.types.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class MethodTable {
    private Map<String, Map<MethodSignature, Operator>> methodTable;

    public MethodTable() {
        this.methodTable = new HashMap<>();
    }

    public void put(Operator operator, String name, Type targetType, Type returnType, Type... parameterTypes) {
        if (methodTable.get(name) == null) {
            methodTable.put(name, new HashMap<>());
        }

        methodTable.get(name).put(MethodSignatureBuilder.method(name)
                        .withReturnType(returnType)
                        .withTargetType(targetType)
                        .withParameterTypes(parameterTypes)
                        .build(),
                operator);
    }

    public Operator get(MethodSignature methodSignature) {
        return methodTable.get(methodSignature.getName()).get(methodSignature);
    }

    public MethodSignature lookupMethod(String name, Type targetType, Type... parameterTypes) {
        Map<MethodSignature, Operator> methods = methodTable.get(name);
        Map<MethodSignature, Integer> scores = new HashMap<>(methods.size());

        for (MethodSignature methodSignature : methods.keySet()) {
            scores.put(methodSignature, score(methodSignature, targetType, parameterTypes));
        }

        List<MethodSignature> potentialMatches = new ArrayList<>();
        int minScore = Integer.MAX_VALUE;
        for (MethodSignature methodSignature : scores.keySet()) {
            int currentScore = scores.get(methodSignature);
            if (currentScore < minScore) {
                potentialMatches.clear();
                potentialMatches.add(methodSignature);
            }
        }

        if (potentialMatches.size() == 0) {
            throw new IllegalStateException("Method not found.");
        } else if (potentialMatches.size() > 1) {
            throw new IllegalStateException("Too many potential matches (" + potentialMatches.size() + ").");
        }

        return potentialMatches.get(0);
    }

    private int score(MethodSignature signature, Type targetType, Type... parameterTypes) {
        int targetTypeScore = typeScore(signature.getTargetType(), targetType);

        if (targetTypeScore < 0) {
            return -1;
        }

        if (signature.getParameterTypes().size() != parameterTypes.length) {
            return -1;
        }

        Iterator<Type> destination = signature.getParameterTypes().iterator();
        Iterator<Type> source = Arrays.asList(parameterTypes).iterator();

        int parameterTypesScore = 0;
        while (destination.hasNext() && source.hasNext()) {
            Type destinationType = destination.next();
            Type sourceType = source.next();

            parameterTypesScore += typeScore(destinationType, sourceType);
        }

        return targetTypeScore + parameterTypesScore;
    }

    private int typeScore(Type destination, Type source) {
        if (source == destination) {
            return 0;
        }

        return -1;
    }
}
