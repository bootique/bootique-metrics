/**
 *  Licensed to ObjectStyle LLC under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ObjectStyle LLC licenses
 *  this file to you under the Apache License, Version 2.0 (the
 *  “License”); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.bootique.metrics.health.check;

/**
 * @since 0.26
 */
public class Threshold<T extends Comparable<T>> implements Comparable<Threshold<T>> {

    private ThresholdType type;
    private T value;

    public Threshold(ThresholdType type, T value) {
        this.type = type;
        this.value = value;
    }

    public int compareTo(T otherValue) {
        // nulls-first compare...
        if (value == null) {
            return otherValue != null ? -1 : 0;
        }

        if (otherValue == null) {
            return 1;
        }

        return value.compareTo(otherValue);
    }

    @Override
    public int compareTo(Threshold<T> o) {
        return compareTo(o.value);
    }

    public T getValue() {
        return value;
    }

    public ThresholdType getType() {
        return type;
    }

    @Override
    public String toString() {

        StringBuilder out = new StringBuilder();
        out.append(type.name().toLowerCase())
                .append(":")
                .append(value);

        return out.toString();
    }
}
