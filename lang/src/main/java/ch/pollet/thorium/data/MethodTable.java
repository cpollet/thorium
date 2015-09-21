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

package ch.pollet.thorium.data;

import ch.pollet.thorium.execution.MethodBody;
import ch.pollet.thorium.types.Type;
import ch.pollet.thorium.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Christophe Pollet
 */
public class MethodTable {
    private Map<String, Map<MethodSignature, MethodBody>> methodTable;
    private Map<String, Method> cache;

    public MethodTable() {
        this.methodTable = new HashMap<>();
        this.cache = new HashMap<>();
    }

    public void put(String name, MethodBody methodBody, Type targetType, Type returnType, Type... parameterTypes) {
        if (methodTable.get(name) == null) {
            methodTable.put(name, new HashMap<>());
        }

        MethodSignature methodSignature = MethodSignatureBuilder.method(name)
                .withReturnType(returnType)
                .withTargetType(targetType)
                .withParameterTypes(parameterTypes)
                .build();

        methodTable.get(name).put(methodSignature, methodBody);

        cache.put(getCacheKey(name, targetType, parameterTypes), new Method(methodSignature, methodBody));
    }

    private String getCacheKey(String name, Type targetType, Type... parameterTypes) {
        return targetType.toString() + "." + name + "(" + CollectionUtils.concat(parameterTypes) + ")";
    }

    // TODO review null/exception handling
    public Method lookupMethod(String name, Type targetType, Type... parameterTypes) {
        String cacheKey = getCacheKey(name, targetType, parameterTypes);

        if (cache.containsKey(cacheKey)) {
            return cache.get(cacheKey);
        }

        Map<MethodSignature, MethodBody> methods = methodTable.get(name);

        if (methods == null) {
            methods = Collections.emptyMap();
        }

        Map<MethodSignature, Integer> scores = new HashMap<>(methods.size());

        for (MethodSignature methodSignature : methods.keySet()) {
            scores.put(methodSignature, score(methodSignature, targetType, parameterTypes));
        }

        MethodSignature signature = getMatch(scores);
        Method method = new Method(signature, methodTable.get(name).get(signature));

        cache.put(cacheKey, method);

        return method;
    }

    /**
     * Returns the scope of a MethodSignature against targetType and parameterTypes. The score is the distance between
     * the method signature (target and formal parameters) and the required target and actual parameters.
     * <p>
     * Each type transformation adds 1 to the final score. For instance, having Integer? in formal parameter and
     * Integer as actual parameter type adds 1 to the score.
     * <p>
     * The lower the best, but a score of -1 means the method signature is not compatible.
     */
    private int score(MethodSignature signature, Type targetType, Type... parameterTypes) {
        int targetTypeScore = typeScore(signature.getTargetType(), targetType);

        if (targetTypeScore < 0) {
            return -1;
        }

        if (signature.getParameterTypes().size() != parameterTypes.length) {
            return -1;
        }

        Iterator<Type> destinations = signature.getParameterTypes().iterator();
        Iterator<Type> sources = Arrays.asList(parameterTypes).iterator();

        int parameterTypesScore = 0;
        while (destinations.hasNext() && sources.hasNext()) {
            Type destination = destinations.next();
            Type source = sources.next();

            int score = typeScore(destination, source);

            if (score < 0) {
                return -1;
            }

            parameterTypesScore += score;
        }

        return targetTypeScore + parameterTypesScore;
    }

    private int typeScore(Type destination, Type source) {
        if (destination == source) {
            return 0;
        } else if (destination.nonNullable() == source) {
            return 1;
        }

        return -1;
    }

    /**
     * Returns the signature with the lowest positive score.
     */
    private MethodSignature getMatch(Map<MethodSignature, Integer> scores) {
        List<MethodSignature> potentialMatches = new ArrayList<>();
        int minScore = Integer.MAX_VALUE;

        for (Map.Entry<MethodSignature, Integer> entry : scores.entrySet()) {
            int currentScore = entry.getValue();

            if (currentScore > -1 && currentScore < minScore) {
                minScore = currentScore;
                potentialMatches.clear();
                potentialMatches.add(entry.getKey());
            } else if (currentScore == minScore) {
                potentialMatches.add(entry.getKey());
            }
        }

        if (potentialMatches.isEmpty()) {
            throw new MethodNotFoundException("Method not found.", potentialMatches);
        } else if (potentialMatches.size() > 1) {
            throw new MethodNotFoundException("Too many potential matches (" + potentialMatches.size() + ").", potentialMatches);
        }

        return potentialMatches.get(0);
    }
}
