/*
 * The MIT License
 *
 * Copyright (c) 2016, Chad Rosenquist
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.github.chadrosenquist;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * <pre>
 * This class is used to randomly generate a password with constraints
 * and is normally used to reset someone's password.
 * </pre>
 * <pre>
 * This class is immutable, with the exception of the random number generator.
 * Each time generateARandomPassword() is called, only the random number generator is changed.
 * </pre>
 * <pre>
 * This class is thread safe.
 * </pre>
 * <pre>
 * This class uses the Builder pattern.  Example:
 * </pre>
 * <pre>
 * {@code
 *     final GeneratePassword.Builder builder = new GeneratePassword.Builder();
 *     builder.length(12);
 *     builder.minUpperCase(1);
 *     builder.minLowerCase(1);
 *     builder.minDigits(2);
 *     builder.minSpecialChars(1);
 *     builder.specialChars("!@#$%^&*");
 *     final GeneratePassword generatePassword = builder.build();
 *     System.out.println(generatePassword.generateARandomPassword());
 * }
 * </pre>
 * 
 */
public class GeneratePassword {
    private static final Logger LOGGER = Logger.getLogger(GeneratePassword.class.getName());
    
    private final int length;
    private final int minUpperCase;
    private final int minLowerCase;
    private final int minAlphabeticChars;
    private final int minDigits;
    private final int minSpecialChars;
    private final int minDistinctChars;
    private final Random random;    
    private final String specialChars;
    private final Collection<String> excludeWords;
    
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String DEFAULT_SPECIAL_CHARS = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    private static final int MAX_RETRIES = 1000;
    
    /**
     * This class is the builder for GeneratePassword.
     */
    public static class Builder {
        private int length = 8;
        private int minUpperCase = 0;
        private int minLowerCase = 0;
        private int minAlphabeticChars = 0;
        private int minDigits = 0;
        private int minSpecialChars = 0;
        private int minDistinctChars = 0;   
        private Long seed = null;
        private String specialChars = DEFAULT_SPECIAL_CHARS;
        private Collection<String> excludeWords = null;
        
        /**
         * Constructs an empty builder.
         * <pre>
         * The following defaults are used:
         * </pre>
         * <pre>
         * {@code
         * length = 8
         * minUpperCase = 0
         * minLowerCase = 0
         * minAlphabeticChars = 0
         * minDigits = 0
         * minSpecialChars = 0
         * minDistinctChars = 0
         * specialChars = !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
         * excludeWords = words to exclude
         * }
         * </pre>
         * 
         */
        public Builder() {
            
        }
        
        /**
         * Set password length.
         * 
         * @param length Password's length
         * @return this builder object
         * Can be used to chain setter methods together.
         * For example:
         * <pre>
         * builder.length(10).minUpperCase(2).minDigits(1);
         * </pre>
         */
        public Builder length(int length) {
            this.length = length;
            return this;
        }
        
        /**
         * Set minimum upper case characters [A-Z].
         * @param minUpperCase minimum upper case characters
         * @return this builder object
         */
        public Builder minUpperCase(int minUpperCase) {
            this.minUpperCase = minUpperCase;
            return this;
        }
        
        /**
         * Set minimum lower case characters [a-z].
         * @param minLowerCase minimum lower case characters
         * @return this builder object
         */
        public Builder minLowerCase(int minLowerCase) {
            this.minLowerCase = minLowerCase;
            return this;
        }
        
        /**
         * Set minimum alphabetic characters [a-z][A-Z].
         * @param minAlphabeticChars minimum alphabetic characters
         * @return this builder object
         */
        public Builder minAlphabeticChars(int minAlphabeticChars) {
            this.minAlphabeticChars = minAlphabeticChars;
            return this;
        }
        
        /**
         * Set minimum digits [0-9].
         * @param minDigits minimum digits
         * @return this builder object
         */
        public Builder minDigits(int minDigits) {
            this.minDigits = minDigits;
            return this;
        }
        
        /**
         * Set minimum special characters.
         * @param minSpecialChars minimum special characters
         * @return this builder object
         */
        public Builder minSpecialChars(int minSpecialChars) {
            this.minSpecialChars = minSpecialChars;
            return this;
        }
        
        /**
         * Set minimum distinct characters.
         * 
         * For example, if a password length is 8 and minimum distinct
         * is 8, all 8 characters must be different.  If a password length
         * is 10 and minimum distinct is 0, all 10 characters can be the same.
         * @param minDistinctChars minimum distinct characters
         * @return this builder object
         */
        public Builder minDistinctChars(int minDistinctChars) {
            this.minDistinctChars = minDistinctChars;
            return this;
        }
        /**
         * Sets the special characters that are allowed in a password.
         * The default special characters are:
         * {@code
         * !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
         * }
         * 
         * @param specialChars string containing the list of special characters
         * @return this builder object
         */        
        public Builder specialChars(String specialChars) {
            this.specialChars = specialChars;
            return this;
        }
        
        /**
         * Sets the seed for the random number generator.
         * Set the seed for automated testing.
         * 
         * @param seed seed for random number generator
         * @return this builder object
         */
        public Builder seed(Long seed) {
            this.seed = seed;
            return this;
        }
        
        /**
         * Sets a {@code Collection<String>} of words to exclude from the password.
         * 
         * @param excludeWords collection of words to exclude
         * @return this builder object
         */
        public Builder excludeWords(Collection<String> excludeWords) {
            this.excludeWords = excludeWords;
            return this;
        }
        
        /**
         * Builds the GeneratePassword object.
         * 
         * @return newly built GeneratePassword
         * @throws NullPointerException if {@code (specialChars == null)}
         * @throws IllegalArgumentException if {@code (specialChars.length() < 1)}
         * @throws IllegalArgumentException if {@code !((minUpperCase + minLowerCase + minDigits + minSpecialChars) <= length)}
         * @throws IllegalArgumentException if {@code !(minDistinctChars <= length)}
         * @throws UnsupportedOperationException if a valid password cannot be generated because of excludeWords or minDistinctChars
         * are too restrictive
         */
        public GeneratePassword build() {
            return new GeneratePassword(this);
        }
    }
    
    private GeneratePassword(final Builder builder) {        
        this.random = new Random();
        if (builder.seed != null)
            this.random.setSeed(builder.seed);
        this.length = builder.length;
        this.minUpperCase = builder.minUpperCase;
        this.minLowerCase = builder.minLowerCase;
        this.minAlphabeticChars = builder.minAlphabeticChars;
        this.minDigits = builder.minDigits;
        this.minSpecialChars = builder.minSpecialChars;
        this.minDistinctChars = builder.minDistinctChars;
        this.specialChars = builder.specialChars;
        if (builder.excludeWords == null) {
            // create an empty collection
            this.excludeWords = new LinkedHashSet<String>();
        }
        else {
            // Make a shallow copy of the collection, removing any duplicates.
            this.excludeWords = new LinkedHashSet<String>(builder.excludeWords);
        }
            
        
        validateClassVariables();
    }
    
    private void validateClassVariables() {
        if (specialChars == null)
            throw new NullPointerException("specialChars cannot be null.");
        if (specialChars.length() < 1)
            throw new IllegalArgumentException("specialChars must contain at least 1 character.");
        
        // Make sure the sum of all minimums is less than or equal to the password length.
        if (!((minUpperCase + minLowerCase + minDigits + minSpecialChars) <= length))
            throw new IllegalArgumentException("The sum of all minimums must be less than or equal to the length, " + length);
        
        // Make sure minDistinctChars less than or equal to the password length.
        if (!(minDistinctChars <= length))
            throw new IllegalArgumentException("The minimum distinct characters must be less than or equal to the length, " + length);       
    }
    
    /**
     * This method generates a random password based on the password rules set by the builder.
     * 
     * @return String containing the password
     */
    public String generateARandomPassword() {
        // Generate a password and check if it contains any of the words in excludeWords.
        // If it does, generate again.
        String password;
        
        int retries = 0;
        DO:
        do {
            retries++;
            password = generateOnePassword();            
            
            for (String word : excludeWords) {
                if ((word != null) && (word.length() >= 1)){           
                    if (password.contains(word)){
                        // The password contains a word that should be excluded.
                        // Generate a new password.
                        continue DO;
                    }
                }
            }
            
            // Password does not contain any of the exclude words.
            // Valid password found so exit.
            break DO;
        } while (retries < MAX_RETRIES);
        
        // If retry limit is reached, log an error and use the last password.
        if (retries == MAX_RETRIES) {
            LOGGER.warning("Failed to generate a password because excludeWords is too restrictive.");
        }
        
        return password;
    }   

    /**
     * Randomly generates one password.
     * 
     * @return password
     */
    private String generateOnePassword() {
        final char[] password = new char[length];  
        final HashMap<Character,Boolean> hasCharBeenUsed = new HashMap<Character,Boolean>();
        boolean excludeUsedChars = false;               
        
        // Generate random characters is the following order:
        //   upper -> lower -> digits -> special -> alphabetic (upper+lower) -> everything else
        int upper = minUpperCase;
        int lower = minLowerCase;
        int digits = minDigits;
        int special = minSpecialChars;
        int alphabetic = minAlphabeticChars;
        int distinct = minDistinctChars;

        FOR:
        for (int count=0; count<length; count++) {
            // Check if minimum distinct character requirement is met.
            // If not, set excludeUsedChars to true.  This will cause
            // randomChar() to not used a character already used.
            // If minimum is met, pick any valid character.
            if (distinct > 0) {
                excludeUsedChars = true;
                distinct--;
            }
            else {
                excludeUsedChars = false;                
            }
            
            if (upper > 0) {
                password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, true, false, false, false);
                upper--;
                alphabetic--;
                continue FOR;
            }         
            if (lower > 0) {
                password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, false, true, false, false);
                lower--;
                alphabetic--;
                continue FOR;
            }
            if (digits > 0) {
                password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, false, false, true, false);
                digits--;
                continue FOR;
            }
            if (special > 0) {
                password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, false, false, false, true);
                special--;
                continue FOR;
            }
            if (alphabetic > 0) {
                password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, true, true, false, false);
                alphabetic--;
                continue FOR;                
            }
            password[count] = randomChar(hasCharBeenUsed, excludeUsedChars, true, true, true, true);
        }
        
        shufflePassword(password);
        
        return String.valueOf(password);
    }

    /**
     * Randomly shuffles the current password.
     */
    private void shufflePassword(final char[] password) {
        for (int index=password.length-1; index>0; index--) {
            int swapIndex=random.nextInt(index+1);
            char tmp = password[swapIndex];
            password[swapIndex] = password[index];
            password[index] = tmp;
        }        
    }

    /**
     * Picks a character at random.
     * 
     * @param hasCharBeenUsed Entry exists if the character has already been used.
     * @param excludeUsedChars True if a character cannot be reused.
     * @param upper Allow upper case.
     * @param lower Allow lower case.
     * @param digits Allow digits.
     * @param special Allow special characters.
     * @return Character picked at random.
     */
    private char randomChar( final Map<Character,Boolean> hasCharBeenUsed,
                             boolean excludeUsedChars,
                             boolean upper,
                             boolean lower,
                             boolean digits,
                             boolean special
                             ) {
        String validChars = new String("");
        
        if (upper)
            validChars = strCatExclude(validChars, UPPER_CASE, hasCharBeenUsed, excludeUsedChars);
        if (lower)
            validChars = strCatExclude(validChars, LOWER_CASE, hasCharBeenUsed, excludeUsedChars);            
        if (digits)
            validChars = strCatExclude(validChars, DIGITS, hasCharBeenUsed, excludeUsedChars);
        if (special)
            validChars = strCatExclude(validChars, specialChars, hasCharBeenUsed, excludeUsedChars);
        
        // Pick a character at random out of the list of valid characters.
        if (validChars.length() <= 0)
            throw new UnsupportedOperationException("No characters left to pick from.  Make sure minimum distinct characters it not set too high.");
        int randomIndex = random.nextInt(validChars.length());
        char pickedChar = validChars.charAt(randomIndex);
        
        // Mark the character as used.
        hasCharBeenUsed.put(pickedChar, true);
        
        return pickedChar;
    }

    /**
     * Similar to strcat(), but if the excludeUsedChars flag is set to true,
     * do not copy any characters in hasCharBeenUsed.
     * @param dest Destination string
     * @param source Source string
     * @return Concatenated string.
     * @param hasCharBeenUsed Entry exists if the character has already been used.
     * @param excludeUsedChars True if a character cannot be reused.     * 
     */
    private String strCatExclude(String dest,
                                 String source,
                                 final Map<Character,Boolean> hasCharBeenUsed,
                                 boolean excludeUsedChars) {                
        if (excludeUsedChars) {
            StringBuilder returnString = new StringBuilder();
            returnString.append(dest);
            for (int index=0; index<source.length(); index++) {
                if (!hasCharBeenUsed.containsKey(source.charAt(index)))
                    returnString.append(source.charAt(index));
            }
            return returnString.toString();
        }
        else {
            return dest + source;       
        }                    
    }
}
