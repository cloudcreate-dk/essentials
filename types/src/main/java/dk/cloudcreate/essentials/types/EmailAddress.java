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

package dk.cloudcreate.essentials.types;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;

/**
 * Immutable Email address value type which supports configurable validation.<br>
 * Default {@link EmailAddressValidator} is the {@link NonValidatingEmailAddressValidator}, but you can supply your own {@link EmailAddressValidator} using
 * the {@link #setValidator(EmailAddressValidator)} method.
 */
public class EmailAddress extends CharSequenceType<EmailAddress> {
    private static EmailAddressValidator VALIDATOR = new NonValidatingEmailAddressValidator();

    /**
     * Get the {@link EmailAddressValidator} used to validate email address<br>
     * Default validator is {@link NonValidatingEmailAddressValidator}
     *
     * @return the {@link EmailAddressValidator} used to validate email address
     */
    public static EmailAddressValidator getValidator() {
        return VALIDATOR;
    }

    /**
     * Set the {@link EmailAddressValidator} that will be used to validate email address
     *
     * @param validator the {@link EmailAddressValidator} that will be used to validate email address
     */
    public static void setValidator(EmailAddressValidator validator) {
        EmailAddress.VALIDATOR = requireNonNull(validator, "You must supply an EmailAddressValidator instance");
    }

    /**
     * Create a typed {@link EmailAddress} for an email address string value
     *
     * @param emailAddress the email address
     * @throws IllegalArgumentException in case the email address is null
     * @throws RuntimeException         in case the email address is not invalid. The actual exception depends on the {@link EmailAddressValidator} implementation used
     */
    public EmailAddress(CharSequence emailAddress) {
        super(validate(emailAddress));
    }

    private static String validate(CharSequence emailAddress) {
        requireNonNull(emailAddress, "emailAddress is null");
        var emailAsString = emailAddress.toString();
        VALIDATOR.validate(emailAsString);
        return emailAsString;
    }

    /**
     * Convert a string Email address to a typed {@link EmailAddress}
     *
     * @param emailAddress the email address
     * @return the typed {@link EmailAddress} with the <code>emailAddress</code> as value
     * @throws IllegalArgumentException in case the email address is null
     * @throws RuntimeException         in case the email address is not invalid. The actual exception depends on the {@link EmailAddressValidator} implementation used
     */
    public static EmailAddress of(String emailAddress) {
        return new EmailAddress(emailAddress);
    }

    /**
     * Email address validator, which is called by the {@link EmailAddress#EmailAddress(CharSequence)} constructor and {@link EmailAddress#of(String)} method
     */
    @FunctionalInterface
    public interface EmailAddressValidator {
        /**
         * Validate the email value (guaranteed not to be null).<br>
         * This method must throw a {@link RuntimeException} in case the email value isn't valid!
         *
         * @param email the email value that must be validated
         */
        void validate(String email);
    }

    /**
     * The default email validation, which doesn't perform any validation because all useful Email RegExp validations are released on
     * licenses that are non-compatible with the Essentials Apache 2 license
     */
    public static class NonValidatingEmailAddressValidator implements EmailAddressValidator {
        @Override
        public void validate(String email) {
        }
    }
}
