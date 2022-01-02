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

import java.util.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

/**
 * Immutable ISO-3166 2 character country code. Any values provided to the constructor or {@link #of(String)}
 * will be validated for length, will ensure that the code is in UPPER CASE and finally validate that the country code is known
 * by performing a lookup in the set returned from {@link Locale#getISOCountries(Locale.IsoCountryCode)} using 
 * {@link java.util.Locale.IsoCountryCode#PART1_ALPHA2}
 */
public class CountryCode extends CharSequenceType<CountryCode> {
    private static final Set<String> KNOWN_ISO_3166_2_CHARACTER_COUNTRY_CODES = Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2);
    
    /**
     * Create a typed {@link CountryCode} from a String ISO-3166 2 character country code
     *
     * @param countryCode the ISO-3166 2 character country code with the <code>countryCode</code> as UPPER CASE value
     * @throws IllegalArgumentException in case the  ISO-3166 2 character country code is not known or otherwise invalid.
     */
    public CountryCode(CharSequence countryCode) {
        super(validate(countryCode));
    }

    private static String validate(CharSequence countryCode) {
        requireNonNull(countryCode, "countryCode is null");
        if (countryCode.length() != 2) {
            throw new IllegalArgumentException(msg("CountryCode is invalid (must be 2 characters): '{}'", countryCode));
        }

        var upperCaseCountryCode = countryCode.toString().toUpperCase();
        if (!KNOWN_ISO_3166_2_CHARACTER_COUNTRY_CODES.contains(upperCaseCountryCode)) {
            throw new IllegalArgumentException(msg("CountryCode '{}' is not known", countryCode));
        }
        return upperCaseCountryCode;
    }

    /**
     * Convert a String ISO-3166 2 character country code to a typed {@link CountryCode}
     *
     * @param countryCode the ISO-3166 2 character country code
     * @return the typed {@link CountryCode} with the <code>countryCode</code> as UPPER CASE value
     * @throws IllegalArgumentException in case the  ISO-3166 2 character country code is not known or otherwise invalid.
     */
    public static CountryCode of(String countryCode) {
        return new CountryCode(countryCode);
    }

    /**
     * Get the Set of String based ISO-3166 2 character country code's
     * @return the Set of String based ISO-3166 2 character country code's
     */
    public static Set<String> getAllCountryCodes() {
        return KNOWN_ISO_3166_2_CHARACTER_COUNTRY_CODES;
    }
}
