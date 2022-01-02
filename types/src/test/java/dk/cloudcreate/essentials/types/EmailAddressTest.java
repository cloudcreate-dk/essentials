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

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;
import static org.assertj.core.api.Assertions.*;

class EmailAddressTest {

    @Test
    void test_EmailAddress_without_validation() {
        EmailAddress.setValidator(new EmailAddress.NonValidatingEmailAddressValidator());

        assertThat(EmailAddress.of("doe@nonexistingdomain.com").value()).isEqualTo("doe@nonexistingdomain.com");
        assertThat(new EmailAddress("john@nonexistingdomain.com").value()).isEqualTo("john@nonexistingdomain.com");
        assertThatThrownBy(() -> new EmailAddress(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EmailAddress.of(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(new EmailAddress("john@invalid domain.com").value()).isEqualTo("john@invalid domain.com");
    }

    @Test
    void test_EmailAddress_with_test_validation() {
        EmailAddress.setValidator(new TestEmailAddressValidator());

        assertThat(EmailAddress.of("doe@nonexistingdomain.com").value()).isEqualTo("doe@nonexistingdomain.com");
        assertThat(new EmailAddress("john@nonexistingdomain.com").value()).isEqualTo("john@nonexistingdomain.com");
        assertThatThrownBy(() -> new EmailAddress(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EmailAddress.of(null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new EmailAddress("john@invalid domain.com").value())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid");
    }


    private static class TestEmailAddressValidator implements EmailAddress.EmailAddressValidator {
        private final Pattern emailValidationPattern;

        public TestEmailAddressValidator() {
            emailValidationPattern = Pattern.compile("^(.+)@(\\S+)$");
        }

        @Override
        public void validate(String email) {
            if (!emailValidationPattern.matcher(email)
                                       .matches()) {
                throw new IllegalArgumentException(msg("Invalid email address '{}'", email));
            }
        }
    }

}