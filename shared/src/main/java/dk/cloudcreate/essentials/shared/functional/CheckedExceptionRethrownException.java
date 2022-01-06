/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.cloudcreate.essentials.shared.functional;

/**
 * Used by the various Checked functional interfaces to indicate that a Checked {@link Exception} has been catched and rethrown
 *
 * @see CheckedRunnable
 * @see CheckedConsumer
 * @see CheckedSupplier
 * @see CheckedFunction
 * @see CheckedBiFunction
 * @see CheckedTripleFunction
 */
public class CheckedExceptionRethrownException extends RuntimeException {
    public CheckedExceptionRethrownException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckedExceptionRethrownException(Throwable cause) {
        super(cause);
    }
}
