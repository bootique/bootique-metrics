/*
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.bootique.metrics;

import io.bootique.di.BQModule;
import io.bootique.names.ClassToName;

import java.util.StringJoiner;

/**
 * A helper class to name metrics across Bootique modules. Helps somewhat to enforce consistent naming convention. Note that
 */
public class MetricNaming {

    protected static ClassToName MODULE_NAME_BUILDER = ClassToName
            .builder()
            .stripSuffix("Module")
            .stripSuffix("Instrumented")
            .build();

    private String modulePrefix;

    protected MetricNaming(String modulePrefix) {
        this.modulePrefix = modulePrefix;
    }

    /**
     * Creates a metrics naming builder for a root module class. Note that to generate a proper name, module class
     * should follow a naming convention of "XyzModule" or "XyzInstrumentedModule".
     *
     * @param metricSourceModule a type of module where a given set of metrics originates.
     * @return a {@link MetricNaming} name builder for a specific module.
     */
    public static MetricNaming forModule(Class<? extends BQModule> metricSourceModule) {
        return new MetricNaming("bq." + MODULE_NAME_BUILDER.toName(metricSourceModule));
    }

    public String name(String... names) {

        StringJoiner joiner = new StringJoiner(".").add(modulePrefix);

        for (String name : names) {
            joiner.add(name);
        }

        return joiner.toString();
    }
}
