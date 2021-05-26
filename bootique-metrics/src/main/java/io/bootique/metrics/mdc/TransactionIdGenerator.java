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

package io.bootique.metrics.mdc;


/**
 * Generates fast and mostly unique IDs for the purpose of "business transactions" correlation in the logs. A business
 * transaction may be a web request, a job execution, etc. Since correlation is the main use case here, each ID doesn't
 * have to be globally unique forever and doesn't have to be hard to guess. So implementing generators are normally
 * optimized for performance instead of randomness, uniqueness and security.
 */
public interface TransactionIdGenerator {

    String nextId();
}
