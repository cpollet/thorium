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

package ch.pollet.thorium.evaluation;

import ch.pollet.thorium.types.Type;

import java.util.Objects;

/**
 * @author Christophe Pollet
 */
public class OperationSignature {
    private String operation;
    private Type leftType;
    private Type rightType;

    public OperationSignature(String operation, Type leftType, Type rightType) {
        this.operation = operation;
        this.leftType = leftType;
        this.rightType = rightType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationSignature)) return false;
        OperationSignature that = (OperationSignature) o;
        return Objects.equals(operation, that.operation) &&
                Objects.equals(leftType, that.leftType) &&
                Objects.equals(rightType, that.rightType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, leftType, rightType);
    }
}
