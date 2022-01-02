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

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

/**
 * Immutable ISO-4217 3 character currency code. Any values provided to the constructor or {@link #of(String)}
 * will be validated for length, will ensure that the code is in UPPER CASE and finally validate that the currency code is part of the
 * {@link #getKnownCurrencyCodes()}<br>
 * The Currency Codes in the {@link #getKnownCurrencyCodes()} are created based on list from https://www.iso.org/iso-4217-currency-codes.html<br>
 * Note: There no guarantee that the known currencies ({@link #getKnownCurrencyCodes()}) are correct or up to date, use at own risk.<br>
 * This class is thread safe
 */
public class CurrencyCode extends CharSequenceType<CurrencyCode> {
    private static final ConcurrentSkipListSet<String> KNOWN_CURRENCY_CODES = new ConcurrentSkipListSet<>();

    /**
     * Create a typed {@link CurrencyCode} from a String ISO-4217 3 character currency code with the <code>currencyCode</code> as UPPER CASE value
     * @param currencyCode the ISO-4217 3 character currency code
     * @throws IllegalArgumentException in case the  ISO-4217 3 character currency code is not known or otherwise invalid.
     * @see #addKnownCurrencyCode(String)
     */
    public CurrencyCode(CharSequence currencyCode) {
        super(validate(currencyCode));
    }

    private static String validate(CharSequence currencyCode) {
        requireNonNull(currencyCode, "currencyCode is null");
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException(msg("CurrencyCode is invalid (must be 3 characters): '{}'", currencyCode));
        }

        var upperCaseCurrencyCode = currencyCode.toString().toUpperCase();
        if (!KNOWN_CURRENCY_CODES.contains(upperCaseCurrencyCode)) {
            throw new IllegalArgumentException(msg("CurrencyCode '{}' is not known", currencyCode));
        }
        return upperCaseCurrencyCode;
    }

    /**
     * Get the set of all ISO-4217 3 character currency codes known by this class.<br>
     * All public static final variables of the {@link CurrencyCode} class are automatically added to the set of known currency codes.<br>
     * You can modify the set by using {@link #addKnownCurrencyCode(String)} or {@link #removeKnownCurrencyCode(String)}/{@link #removeKnownCurrencyCode(CurrencyCode)}
     *
     * @return the set of all ISO-4217 3 character currency codes known by this class.
     */
    public static Set<String> getKnownCurrencyCodes() {
        return KNOWN_CURRENCY_CODES;
    }

    /**
     * Add a ISO-4217 3 character currency code as a known currency code.
     *
     * @param currencyCode the currency code to add to the known currency codes
     * @return the currency code as a typed {@link CurrencyCode}
     */
    public static CurrencyCode addKnownCurrencyCode(String currencyCode) {
        requireNonNull(currencyCode, "currencyCode is null");
        if (currencyCode.length() != 3) {
            throw new IllegalArgumentException(msg("CurrencyCode is invalid (must be 3 characters): '{}'", currencyCode));
        }
        KNOWN_CURRENCY_CODES.add(currencyCode.toUpperCase());
        return new CurrencyCode(currencyCode.toUpperCase());
    }

    /**
     * Remove the ISO-4217 3 character currency code as a known currency code.
     *
     * @param currencyCode the currency code to remove from the known currency codes
     */
    public static void removeKnownCurrencyCode(String currencyCode) {
        requireNonNull(currencyCode, "currencyCode is null");
        KNOWN_CURRENCY_CODES.remove(currencyCode);
    }

    /**
     * Remove the ISO-4217 3 character currency code as a known currency code.
     *
     * @param currencyCode the currency code to remove from the known currency codes
     */
    public static void removeKnownCurrencyCode(CurrencyCode currencyCode) {
        requireNonNull(currencyCode, "currencyCode is null");
        removeKnownCurrencyCode(currencyCode.toString());
    }

    /**
     * Convert a String ISO-4217 3 character currency code to a typed {@link CurrencyCode}
     * @param currencyCode the ISO-4217 3 character currency code
     * @return the typed {@link CurrencyCode} with the <code>currencyCode</code> as UPPER CASE value
     * @throws IllegalArgumentException in case the  ISO-4217 3 character currency code is not known or otherwise invalid.
     * @see #addKnownCurrencyCode(String)
     */
    public static CurrencyCode of(String currencyCode) {
        return new CurrencyCode(currencyCode);
    }

    // -------------------------------------------------------------------------------------

    /**
     * Afghani
     */
    public static CurrencyCode AFN = CurrencyCode.addKnownCurrencyCode("AFN");


    /**
     * Lek
     */
    public static CurrencyCode ALL = CurrencyCode.addKnownCurrencyCode("ALL");


    /**
     * Algerian Dinar
     */
    public static CurrencyCode DZD = CurrencyCode.addKnownCurrencyCode("DZD");


    /**
     * Kwanza
     */
    public static CurrencyCode AOA = CurrencyCode.addKnownCurrencyCode("AOA");


    /**
     * East Caribbean Dollar
     */
    public static CurrencyCode XCD = CurrencyCode.addKnownCurrencyCode("XCD");


    /**
     * Argentine Peso
     */
    public static CurrencyCode ARS = CurrencyCode.addKnownCurrencyCode("ARS");


    /**
     * Armenian Dram
     */
    public static CurrencyCode AMD = CurrencyCode.addKnownCurrencyCode("AMD");


    /**
     * Aruban Florin
     */
    public static CurrencyCode AWG = CurrencyCode.addKnownCurrencyCode("AWG");


    /**
     * Australian Dollar
     */
    public static CurrencyCode AUD = CurrencyCode.addKnownCurrencyCode("AUD");


    /**
     * Azerbaijan Manat
     */
    public static CurrencyCode AZN = CurrencyCode.addKnownCurrencyCode("AZN");


    /**
     * Bahamian Dollar
     */
    public static CurrencyCode BSD = CurrencyCode.addKnownCurrencyCode("BSD");


    /**
     * Bahraini Dinar
     */
    public static CurrencyCode BHD = CurrencyCode.addKnownCurrencyCode("BHD");


    /**
     * Taka
     */
    public static CurrencyCode BDT = CurrencyCode.addKnownCurrencyCode("BDT");


    /**
     * Barbados Dollar
     */
    public static CurrencyCode BBD = CurrencyCode.addKnownCurrencyCode("BBD");


    /**
     * Belarusian Ruble
     */
    public static CurrencyCode BYN = CurrencyCode.addKnownCurrencyCode("BYN");


    /**
     * Belize Dollar
     */
    public static CurrencyCode BZD = CurrencyCode.addKnownCurrencyCode("BZD");


    /**
     * CFA Franc BCEAO
     */
    public static CurrencyCode XOF = CurrencyCode.addKnownCurrencyCode("XOF");


    /**
     * Bermudian Dollar
     */
    public static CurrencyCode BMD = CurrencyCode.addKnownCurrencyCode("BMD");


    /**
     * Indian Rupee
     */
    public static CurrencyCode INR = CurrencyCode.addKnownCurrencyCode("INR");


    /**
     * Ngultrum
     */
    public static CurrencyCode BTN = CurrencyCode.addKnownCurrencyCode("BTN");


    /**
     * Boliviano
     */
    public static CurrencyCode BOB = CurrencyCode.addKnownCurrencyCode("BOB");


    /**
     * Mvdol
     */
    public static CurrencyCode BOV = CurrencyCode.addKnownCurrencyCode("BOV");


    /**
     * Convertible Mark
     */
    public static CurrencyCode BAM = CurrencyCode.addKnownCurrencyCode("BAM");


    /**
     * Pula
     */
    public static CurrencyCode BWP = CurrencyCode.addKnownCurrencyCode("BWP");


    /**
     * Norwegian Krone
     */
    public static CurrencyCode NOK = CurrencyCode.addKnownCurrencyCode("NOK");


    /**
     * Brazilian Real
     */
    public static CurrencyCode BRL = CurrencyCode.addKnownCurrencyCode("BRL");


    /**
     * Brunei Dollar
     */
    public static CurrencyCode BND = CurrencyCode.addKnownCurrencyCode("BND");


    /**
     * Bulgarian Lev
     */
    public static CurrencyCode BGN = CurrencyCode.addKnownCurrencyCode("BGN");

    /**
     * Burundi Franc
     */
    public static CurrencyCode BIF = CurrencyCode.addKnownCurrencyCode("BIF");


    /**
     * Cabo Verde Escudo
     */
    public static CurrencyCode CVE = CurrencyCode.addKnownCurrencyCode("CVE");


    /**
     * Riel
     */
    public static CurrencyCode KHR = CurrencyCode.addKnownCurrencyCode("KHR");


    /**
     * CFA Franc BEAC
     */
    public static CurrencyCode XAF = CurrencyCode.addKnownCurrencyCode("XAF");


    /**
     * Canadian Dollar
     */
    public static CurrencyCode CAD = CurrencyCode.addKnownCurrencyCode("CAD");


    /**
     * Cayman Islands Dollar
     */
    public static CurrencyCode KYD = CurrencyCode.addKnownCurrencyCode("KYD");

    /**
     * Chilean Peso
     */
    public static CurrencyCode CLP = CurrencyCode.addKnownCurrencyCode("CLP");


    /**
     * Unidad de Fomento
     */
    public static CurrencyCode CLF = CurrencyCode.addKnownCurrencyCode("CLF");


    /**
     * Yuan Renminbi
     */
    public static CurrencyCode CNY = CurrencyCode.addKnownCurrencyCode("CNY");

    /**
     * Colombian Peso
     */
    public static CurrencyCode COP = CurrencyCode.addKnownCurrencyCode("COP");


    /**
     * Unidad de Valor Real
     */
    public static CurrencyCode COU = CurrencyCode.addKnownCurrencyCode("COU");


    /**
     * Comorian Franc
     */
    public static CurrencyCode KMF = CurrencyCode.addKnownCurrencyCode("KMF");


    /**
     * Congolese Franc
     */
    public static CurrencyCode CDF = CurrencyCode.addKnownCurrencyCode("CDF");


    /**
     * New Zealand Dollar
     */
    public static CurrencyCode NZD = CurrencyCode.addKnownCurrencyCode("NZD");


    /**
     * Costa Rican Colon
     */
    public static CurrencyCode CRC = CurrencyCode.addKnownCurrencyCode("CRC");

    /**
     * Kuna
     */
    public static CurrencyCode HRK = CurrencyCode.addKnownCurrencyCode("HRK");


    /**
     * Cuban Peso
     */
    public static CurrencyCode CUP = CurrencyCode.addKnownCurrencyCode("CUP");


    /**
     * Peso Convertible
     */
    public static CurrencyCode CUC = CurrencyCode.addKnownCurrencyCode("CUC");


    /**
     * Netherlands Antillean Guilder
     */
    public static CurrencyCode ANG = CurrencyCode.addKnownCurrencyCode("ANG");


    /**
     * Czech Koruna
     */
    public static CurrencyCode CZK = CurrencyCode.addKnownCurrencyCode("CZK");


    /**
     * Danish Krone
     */
    public static CurrencyCode DKK = CurrencyCode.addKnownCurrencyCode("DKK");


    /**
     * Djibouti Franc
     */
    public static CurrencyCode DJF = CurrencyCode.addKnownCurrencyCode("DJF");


    /**
     * Dominican Peso
     */
    public static CurrencyCode DOP = CurrencyCode.addKnownCurrencyCode("DOP");


    /**
     * Egyptian Pound
     */
    public static CurrencyCode EGP = CurrencyCode.addKnownCurrencyCode("EGP");


    /**
     * El Salvador Colon
     */
    public static CurrencyCode SVC = CurrencyCode.addKnownCurrencyCode("SVC");


    /**
     * Nakfa
     */
    public static CurrencyCode ERN = CurrencyCode.addKnownCurrencyCode("ERN");


    /**
     * Lilangeni
     */
    public static CurrencyCode SZL = CurrencyCode.addKnownCurrencyCode("SZL");


    /**
     * Ethiopian Birr
     */
    public static CurrencyCode ETB = CurrencyCode.addKnownCurrencyCode("ETB");


    /**
     * Falkland Islands Pound
     */
    public static CurrencyCode FKP = CurrencyCode.addKnownCurrencyCode("FKP");


    /**
     * Fiji Dollar
     */
    public static CurrencyCode FJD = CurrencyCode.addKnownCurrencyCode("FJD");


    /**
     * CFP Franc
     */
    public static CurrencyCode XPF = CurrencyCode.addKnownCurrencyCode("XPF");


    /**
     * Dalasi
     */
    public static CurrencyCode GMD = CurrencyCode.addKnownCurrencyCode("GMD");


    /**
     * Lari
     */
    public static CurrencyCode GEL = CurrencyCode.addKnownCurrencyCode("GEL");


    /**
     * Ghana Cedi
     */
    public static CurrencyCode GHS = CurrencyCode.addKnownCurrencyCode("GHS");


    /**
     * Gibraltar Pound
     */
    public static CurrencyCode GIP = CurrencyCode.addKnownCurrencyCode("GIP");

    /**
     * Euro
     */
    public static CurrencyCode EUR = CurrencyCode.addKnownCurrencyCode("EUR");


    /**
     * US Dollar
     */
    public static CurrencyCode USD = CurrencyCode.addKnownCurrencyCode("USD");


    /**
     * Quetzal
     */
    public static CurrencyCode GTQ = CurrencyCode.addKnownCurrencyCode("GTQ");


    /**
     * Pound Sterling
     */
    public static CurrencyCode GBP = CurrencyCode.addKnownCurrencyCode("GBP");


    /**
     * Guinean Franc
     */
    public static CurrencyCode GNF = CurrencyCode.addKnownCurrencyCode("GNF");


    /**
     * Guyana Dollar
     */
    public static CurrencyCode GYD = CurrencyCode.addKnownCurrencyCode("GYD");


    /**
     * Gourde
     */
    public static CurrencyCode HTG = CurrencyCode.addKnownCurrencyCode("HTG");


    /**
     * Lempira
     */
    public static CurrencyCode HNL = CurrencyCode.addKnownCurrencyCode("HNL");


    /**
     * Hong Kong Dollar
     */
    public static CurrencyCode HKD = CurrencyCode.addKnownCurrencyCode("HKD");


    /**
     * Forint
     */
    public static CurrencyCode HUF = CurrencyCode.addKnownCurrencyCode("HUF");


    /**
     * Iceland Krona
     */
    public static CurrencyCode ISK = CurrencyCode.addKnownCurrencyCode("ISK");


    /**
     * Rupiah
     */
    public static CurrencyCode IDR = CurrencyCode.addKnownCurrencyCode("IDR");


    /**
     * SDR (Special Drawing Right)
     */
    public static CurrencyCode XDR = CurrencyCode.addKnownCurrencyCode("XDR");


    /**
     * Iranian Rial
     */
    public static CurrencyCode IRR = CurrencyCode.addKnownCurrencyCode("IRR");


    /**
     * Iraqi Dinar
     */
    public static CurrencyCode IQD = CurrencyCode.addKnownCurrencyCode("IQD");


    /**
     * New Israeli Sheqel
     */
    public static CurrencyCode ILS = CurrencyCode.addKnownCurrencyCode("ILS");


    /**
     * Jamaican Dollar
     */
    public static CurrencyCode JMD = CurrencyCode.addKnownCurrencyCode("JMD");


    /**
     * Yen
     */
    public static CurrencyCode JPY = CurrencyCode.addKnownCurrencyCode("JPY");


    /**
     * Jordanian Dinar
     */
    public static CurrencyCode JOD = CurrencyCode.addKnownCurrencyCode("JOD");


    /**
     * Tenge
     */
    public static CurrencyCode KZT = CurrencyCode.addKnownCurrencyCode("KZT");


    /**
     * Kenyan Shilling
     */
    public static CurrencyCode KES = CurrencyCode.addKnownCurrencyCode("KES");


    /**
     * North Korean Won
     */
    public static CurrencyCode KPW = CurrencyCode.addKnownCurrencyCode("KPW");


    /**
     * Won
     */
    public static CurrencyCode KRW = CurrencyCode.addKnownCurrencyCode("KRW");


    /**
     * Kuwaiti Dinar
     */
    public static CurrencyCode KWD = CurrencyCode.addKnownCurrencyCode("KWD");


    /**
     * Som
     */
    public static CurrencyCode KGS = CurrencyCode.addKnownCurrencyCode("KGS");


    /**
     * Lao Kip
     */
    public static CurrencyCode LAK = CurrencyCode.addKnownCurrencyCode("LAK");


    /**
     * Lebanese Pound
     */
    public static CurrencyCode LBP = CurrencyCode.addKnownCurrencyCode("LBP");


    /**
     * Loti
     */
    public static CurrencyCode LSL = CurrencyCode.addKnownCurrencyCode("LSL");


    /**
     * Rand
     */
    public static CurrencyCode ZAR = CurrencyCode.addKnownCurrencyCode("ZAR");


    /**
     * Liberian Dollar
     */
    public static CurrencyCode LRD = CurrencyCode.addKnownCurrencyCode("LRD");


    /**
     * Libyan Dinar
     */
    public static CurrencyCode LYD = CurrencyCode.addKnownCurrencyCode("LYD");


    /**
     * Swiss Franc
     */
    public static CurrencyCode CHF = CurrencyCode.addKnownCurrencyCode("CHF");


    /**
     * Pataca
     */
    public static CurrencyCode MOP = CurrencyCode.addKnownCurrencyCode("MOP");


    /**
     * Denar
     */
    public static CurrencyCode MKD = CurrencyCode.addKnownCurrencyCode("MKD");


    /**
     * Malagasy Ariary
     */
    public static CurrencyCode MGA = CurrencyCode.addKnownCurrencyCode("MGA");


    /**
     * Malawi Kwacha
     */
    public static CurrencyCode MWK = CurrencyCode.addKnownCurrencyCode("MWK");


    /**
     * Malaysian Ringgit
     */
    public static CurrencyCode MYR = CurrencyCode.addKnownCurrencyCode("MYR");


    /**
     * Rufiyaa
     */
    public static CurrencyCode MVR = CurrencyCode.addKnownCurrencyCode("MVR");


    /**
     * Ouguiya
     */
    public static CurrencyCode MRU = CurrencyCode.addKnownCurrencyCode("MRU");


    /**
     * Mauritius Rupee
     */
    public static CurrencyCode MUR = CurrencyCode.addKnownCurrencyCode("MUR");


    /**
     * ADB Unit of Account
     */
    public static CurrencyCode XUA = CurrencyCode.addKnownCurrencyCode("XUA");

    /**
     * Mexican Peso
     */
    public static CurrencyCode MXN = CurrencyCode.addKnownCurrencyCode("MXN");


    /**
     * Mexican Unidad de Inversion (UDI)
     */
    public static CurrencyCode MXV = CurrencyCode.addKnownCurrencyCode("MXV");


    /**
     * Moldovan Leu
     */
    public static CurrencyCode MDL = CurrencyCode.addKnownCurrencyCode("MDL");


    /**
     * Tugrik
     */
    public static CurrencyCode MNT = CurrencyCode.addKnownCurrencyCode("MNT");

    /**
     * Moroccan Dirham
     */
    public static CurrencyCode MAD = CurrencyCode.addKnownCurrencyCode("MAD");


    /**
     * Mozambique Metical
     */
    public static CurrencyCode MZN = CurrencyCode.addKnownCurrencyCode("MZN");


    /**
     * Kyat
     */
    public static CurrencyCode MMK = CurrencyCode.addKnownCurrencyCode("MMK");


    /**
     * Namibia Dollar
     */
    public static CurrencyCode NAD = CurrencyCode.addKnownCurrencyCode("NAD");


    /**
     * Nepalese Rupee
     */
    public static CurrencyCode NPR = CurrencyCode.addKnownCurrencyCode("NPR");


    /**
     * Cordoba Oro
     */
    public static CurrencyCode NIO = CurrencyCode.addKnownCurrencyCode("NIO");

    /**
     * Naira
     */
    public static CurrencyCode NGN = CurrencyCode.addKnownCurrencyCode("NGN");


    /**
     * Rial Omani
     */
    public static CurrencyCode OMR = CurrencyCode.addKnownCurrencyCode("OMR");


    /**
     * Pakistan Rupee
     */
    public static CurrencyCode PKR = CurrencyCode.addKnownCurrencyCode("PKR");


    /**
     * Kina
     */
    public static CurrencyCode PGK = CurrencyCode.addKnownCurrencyCode("PGK");


    /**
     * Guarani
     */
    public static CurrencyCode PYG = CurrencyCode.addKnownCurrencyCode("PYG");


    /**
     * Sol
     */
    public static CurrencyCode PEN = CurrencyCode.addKnownCurrencyCode("PEN");


    /**
     * Philippine Peso
     */
    public static CurrencyCode PHP = CurrencyCode.addKnownCurrencyCode("PHP");

    /**
     * Zloty
     */
    public static CurrencyCode PLN = CurrencyCode.addKnownCurrencyCode("PLN");


    /**
     * Qatari Rial
     */
    public static CurrencyCode QAR = CurrencyCode.addKnownCurrencyCode("QAR");


    /**
     * Romanian Leu
     */
    public static CurrencyCode RON = CurrencyCode.addKnownCurrencyCode("RON");


    /**
     * Russian Ruble
     */
    public static CurrencyCode RUB = CurrencyCode.addKnownCurrencyCode("RUB");


    /**
     * Rwanda Franc
     */
    public static CurrencyCode RWF = CurrencyCode.addKnownCurrencyCode("RWF");


    /**
     * Saint Helena Pound
     */
    public static CurrencyCode SHP = CurrencyCode.addKnownCurrencyCode("SHP");


    /**
     * Tala
     */
    public static CurrencyCode WST = CurrencyCode.addKnownCurrencyCode("WST");


    /**
     * Dobra
     */
    public static CurrencyCode STN = CurrencyCode.addKnownCurrencyCode("STN");


    /**
     * Saudi Riyal
     */
    public static CurrencyCode SAR = CurrencyCode.addKnownCurrencyCode("SAR");


    /**
     * Serbian Dinar
     */
    public static CurrencyCode RSD = CurrencyCode.addKnownCurrencyCode("RSD");


    /**
     * Seychelles Rupee
     */
    public static CurrencyCode SCR = CurrencyCode.addKnownCurrencyCode("SCR");


    /**
     * Leone
     */
    public static CurrencyCode SLL = CurrencyCode.addKnownCurrencyCode("SLL");


    /**
     * Singapore Dollar
     */
    public static CurrencyCode SGD = CurrencyCode.addKnownCurrencyCode("SGD");


    /**
     * Sucre
     */
    public static CurrencyCode XSU = CurrencyCode.addKnownCurrencyCode("XSU");


    /**
     * Solomon Islands Dollar
     */
    public static CurrencyCode SBD = CurrencyCode.addKnownCurrencyCode("SBD");


    /**
     * Somali Shilling
     */
    public static CurrencyCode SOS = CurrencyCode.addKnownCurrencyCode("SOS");


    /**
     * Sri Lanka Rupee
     */
    public static CurrencyCode LKR = CurrencyCode.addKnownCurrencyCode("LKR");


    /**
     * Sudanese Pound
     */
    public static CurrencyCode SDG = CurrencyCode.addKnownCurrencyCode("SDG");


    /**
     * Surinam Dollar
     */
    public static CurrencyCode SRD = CurrencyCode.addKnownCurrencyCode("SRD");


    /**
     * Swedish Krona
     */
    public static CurrencyCode SEK = CurrencyCode.addKnownCurrencyCode("SEK");


    /**
     * WIR Euro
     */
    public static CurrencyCode CHE = CurrencyCode.addKnownCurrencyCode("CHE");


    /**
     * WIR Franc
     */
    public static CurrencyCode CHW = CurrencyCode.addKnownCurrencyCode("CHW");


    /**
     * Syrian Pound
     */
    public static CurrencyCode SYP = CurrencyCode.addKnownCurrencyCode("SYP");


    /**
     * New Taiwan Dollar
     */
    public static CurrencyCode TWD = CurrencyCode.addKnownCurrencyCode("TWD");


    /**
     * Somoni
     */
    public static CurrencyCode TJS = CurrencyCode.addKnownCurrencyCode("TJS");


    /**
     * Tanzanian Shilling
     */
    public static CurrencyCode TZS = CurrencyCode.addKnownCurrencyCode("TZS");


    /**
     * Baht
     */
    public static CurrencyCode THB = CurrencyCode.addKnownCurrencyCode("THB");


    /**
     * Pa’anga
     */
    public static CurrencyCode TOP = CurrencyCode.addKnownCurrencyCode("TOP");


    /**
     * Trinidad and Tobago Dollar
     */
    public static CurrencyCode TTD = CurrencyCode.addKnownCurrencyCode("TTD");


    /**
     * Tunisian Dinar
     */
    public static CurrencyCode TND = CurrencyCode.addKnownCurrencyCode("TND");


    /**
     * Turkish Lira
     */
    public static CurrencyCode TRY = CurrencyCode.addKnownCurrencyCode("TRY");


    /**
     * Turkmenistan New Manat
     */
    public static CurrencyCode TMT = CurrencyCode.addKnownCurrencyCode("TMT");


    /**
     * Uganda Shilling
     */
    public static CurrencyCode UGX = CurrencyCode.addKnownCurrencyCode("UGX");


    /**
     * Hryvnia
     */
    public static CurrencyCode UAH = CurrencyCode.addKnownCurrencyCode("UAH");


    /**
     * UAE Dirham
     */
    public static CurrencyCode AED = CurrencyCode.addKnownCurrencyCode("AED");


    /**
     * US Dollar (Next day)
     */
    public static CurrencyCode USN = CurrencyCode.addKnownCurrencyCode("USN");


    /**
     * Peso Uruguayo
     */
    public static CurrencyCode UYU = CurrencyCode.addKnownCurrencyCode("UYU");


    /**
     * Uruguay Peso en Unidades Indexadas (UI)
     */
    public static CurrencyCode UYI = CurrencyCode.addKnownCurrencyCode("UYI");


    /**
     * Unidad Previsional
     */
    public static CurrencyCode UYW = CurrencyCode.addKnownCurrencyCode("UYW");


    /**
     * Uzbekistan Sum
     */
    public static CurrencyCode UZS = CurrencyCode.addKnownCurrencyCode("UZS");


    /**
     * Vatu
     */
    public static CurrencyCode VUV = CurrencyCode.addKnownCurrencyCode("VUV");


    /**
     * Bolívar Soberano
     */
    public static CurrencyCode VES = CurrencyCode.addKnownCurrencyCode("VES");


    /**
     * Bolívar Soberano
     */
    public static CurrencyCode VED = CurrencyCode.addKnownCurrencyCode("VED");


    /**
     * Dong
     */
    public static CurrencyCode VND = CurrencyCode.addKnownCurrencyCode("VND");


    /**
     * Yemeni Rial
     */
    public static CurrencyCode YER = CurrencyCode.addKnownCurrencyCode("YER");


    /**
     * Zambian Kwacha
     */
    public static CurrencyCode ZMW = CurrencyCode.addKnownCurrencyCode("ZMW");


    /**
     * Zimbabwe Dollar
     */
    public static CurrencyCode ZWL = CurrencyCode.addKnownCurrencyCode("ZWL");

    /**
     * Gold
     */
    public static CurrencyCode XAU = CurrencyCode.addKnownCurrencyCode("XAU");


    /**
     * Palladium
     */
    public static CurrencyCode XPD = CurrencyCode.addKnownCurrencyCode("XPD");


    /**
     * Platinum
     */
    public static CurrencyCode XPT = CurrencyCode.addKnownCurrencyCode("XPT");


    /**
     * Silver
     */
    public static CurrencyCode XAG = CurrencyCode.addKnownCurrencyCode("XAG");
}
