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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

public class GeneratePasswordTest {
    private GeneratePassword.Builder builder;
    private static final long STRESS_TEST_LOOPS = 1000;
    private static final int NUMBER_OF_STRESS_THREADS = 10;
    private static final int STRESS_EXCLUDE_WORDS = 1000;
    private static final int STRESS_EXCLUDE_WORD_LENGTH_MIN = 10;
    private static final int STRESS_EXCLUDE_WORD_LENGTH_MAX = 15;
    
    @Before
    public void setUp() {
        builder = new GeneratePassword.Builder();
    }
    
    @Test (expected=NullPointerException.class)
    public void testSetSpecialChars1() {
        builder.specialChars(null);
        builder.build();
    }

    @Test (expected=IllegalArgumentException.class)
    public void testSetSpecialChars2() {
        builder.specialChars("");
        builder.build();
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testMinLessThanLength() {
        builder.length(12);
        builder.minUpperCase(3);
        builder.minLowerCase(4);
        builder.minDigits(3);
        builder.minSpecialChars(3);
        builder.build();
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testMinDistinctGreaterThanLength() {
        builder.length(10);
        builder.minDistinctChars(11);
        builder.build();
    }    

    @Test
    public void testAllUpperCase1() {
        builder.length(8);
        builder.minUpperCase(8);
        builder.seed(34355L);        
        assertEquals("ONXWDMKW", builder.build().generateARandomPassword());
    }

    @Test
    public void testAllUpperCase2() {
        builder.length(14);
        builder.minUpperCase(14);
        builder.seed(3045328053285032L);
        assertEquals("SDPOZBZJQQNFXV", builder.build().generateARandomPassword());
    }

    @Test
    public void testAllLowerCase() {
        builder.length(10);
        builder.minLowerCase(10);
        builder.seed(435694376L);
        assertEquals("nonyjnwhgh", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testAllAlphabetic() {
        builder.length(20);
        builder.minAlphabeticChars(20);
        builder.seed(650004L);
        assertEquals("SHszrVLueytaGVeaxIRY", builder.build().generateARandomPassword());
    }

    @Test
    public void testAllDigits() {
        builder.length(15);
        builder.minDigits(15);
        builder.seed(6868342575678436457L);
        assertEquals("412566696995779", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testAllSpecials() {
        builder.length(9);
        builder.minSpecialChars(9);
        builder.seed(8623202L);
        assertEquals("')>.!(@\\%", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testNoRestraints() {
        builder.length(100);
        builder.seed(32957235923L);
        assertEquals("CaKt{W@dGq2XFFH5=6T.q}(W1g9B2D#WX:^nS3t\\W7OVMkJrCz{ssEC{{jGw7~j2kA{7(X}<*IZ;d*e$RY~[:[T-|7}X>]KZ9a{N", builder.build().generateARandomPassword());
        builder.seed(235442L);
        assertEquals("U3S\\JMH~O1_*R:5YbHSWY|`^ZD{q%fI*#Nv%<=j7u|MEe6S$Xag<?|;n0^\"Rd[-HH4LiE+0E$<CO_2~ha&P-4ODf/vmFs84ete'^", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testCombo1() {
        builder.length(12);
        builder.seed(43252L);
        builder.minUpperCase(1);
        builder.minLowerCase(1);
        builder.minDigits(1);
        assertEquals("<d%\\~8gcYPR*", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testCombo2() {
        builder.length(10);
        builder.seed(6L);
        builder.minUpperCase(1);
        builder.minLowerCase(1);
        builder.minDigits(1);
        builder.minSpecialChars(1);
        builder.minAlphabeticChars(4);
        assertEquals("Vp'R4g6JLS", builder.build().generateARandomPassword());
    }   
    
    @Test
    public void testMinDistinctAllDigits() {
        builder.length(10);
        builder.minDistinctChars(10);
        builder.minDigits(10);
        builder.seed(660232762L);
        // Each digit will appear exactly once.
        assertEquals("0214359678", builder.build().generateARandomPassword());
    }
    
    @Test (expected=UnsupportedOperationException.class)
    public void testMinDistinctAllDigitsError() {
        builder.length(12);
        builder.minDistinctChars(12);
        builder.minDigits(12);
        // Error, will run out of digits to pick.
        builder.build().generateARandomPassword();
    }
    
    @Test
    public void testMinDistinctHalf() {
        builder.length(20);
        builder.minDistinctChars(10);
        builder.seed(97557893L);
        assertEquals("\"J=|E:m'z1+<CdAI!~/o", builder.build().generateARandomPassword());
    }
    
    @Test
    public void testMinDistinctAll() {
        builder.length(20);
        builder.minDistinctChars(20);
        builder.seed(97557894L);
        assertEquals("K4Z>f,obuTndA)&:IM8J", builder.build().generateARandomPassword());
    }
    
    private static class StressThread implements Runnable {
        private final GeneratePassword generatePassword;
        private final Jiggle jiggle;
        
        public StressThread(final GeneratePassword generatePassword, final Jiggle jiggle) {
            this.generatePassword = generatePassword;
            this.jiggle = jiggle;
        }
               
        public void run() {
            for(long count=0; count < STRESS_TEST_LOOPS; count++) {
                generatePassword.generateARandomPassword();
                jiggle.jiggle();
            }            
        }
        
    }
    
    /**
     * Takes an average of .719 seconds on my computer.
     * @throws InterruptedException
     */
    @Test(timeout=20000)
    public void testStress() throws InterruptedException {
        // Setup GeneratePassword object
        builder.length(300);
        builder.minUpperCase(10);
        builder.minUpperCase(10);
        builder.minAlphabeticChars(100);
        builder.minDigits(5);
        builder.minDistinctChars(10);               
        final Set<String> exclude = randomExcludeWords();
        builder.excludeWords(exclude);
        final GeneratePassword generatePassword = builder.build();
        final Jiggle jiggle = new Jiggle();
        
        // Create multiple threads
        final List<Thread> threads = new LinkedList<Thread>();
        for (int count = 0; count < NUMBER_OF_STRESS_THREADS; count++) {
            threads.add(new Thread(new StressThread(generatePassword, jiggle)));
        }
        
        // Start the threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for threads to finish
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Test passes if it made it this far without crashing.
    }
    
    /**
     * creates a set of random words
     * @return
     */
    private Set<String> randomExcludeWords() {
        final TreeSet<String> exclude = new TreeSet<String>();
        final Random random = new Random();
        
        for (int count = 0; count < STRESS_EXCLUDE_WORDS; count++) {
            byte[] word = new byte[random.nextInt(STRESS_EXCLUDE_WORD_LENGTH_MAX - STRESS_EXCLUDE_WORD_LENGTH_MIN + 1)
                                   + STRESS_EXCLUDE_WORD_LENGTH_MIN];
            random.nextBytes(word);
            exclude.add(new String(word));
        }
        
        exclude.add("dfldjf");
        exclude.add("343");
        return exclude;
    }

    @Test
    public void testExcludeWords() {
        builder.length(8);
        builder.seed(454359L);
        
        // Create list of words to exclude.
        final LinkedList<String> exclude = new LinkedList<String>();
        exclude.add("=?/UL$B(");
        exclude.add("zovNpWJu");
        exclude.add("+/(eZ=4~");
        exclude.add("Q/$+TY@m");
        exclude.add("uJ2*.S)j");
        builder.excludeWords(exclude);
        
        final String password = builder.build().generateARandomPassword();
        assertFalse(password.equals("=?/UL$B("));
        assertFalse(password.equals("zovNpWJu"));
        assertFalse(password.equals("+/(eZ=4~"));
        assertFalse(password.equals("Q/$+TY@m"));
        assertFalse(password.equals("uJ2*.S)j"));
        assertEquals("F$R9^|>b", password);
    }
        
    //@Test(timeout=20000,expected=UnsupportedOperationException.class)
    @Test(timeout=20000)
    public void testExcludeWordRetryLimitReached() {
        builder.length(10);
        builder.minDigits(10);
        final ArrayList<String> exclude = new ArrayList<String>(10);
        exclude.add("0");
        exclude.add("1");
        exclude.add("2");
        exclude.add("3");
        exclude.add("4");
        exclude.add("5");
        exclude.add("6");
        exclude.add("7");
        exclude.add("8");
        exclude.add("9");
        builder.excludeWords(exclude);
        
        // The password must contain 10 digits, but all of the digits are excluded.
        // It is impossible to generate a valid password.
        /*
         * This test case generates a warning message.  The warning message is verified.
         * A mock handler is added to the logger that records the messages.
         * Messages are disabled from going to stdout/stderr.
         */
        final Logger logger = Logger.getLogger(GeneratePassword.class.getName());
        boolean useParentHandlers = logger.getUseParentHandlers();
        logger.setUseParentHandlers(false);
        final Handler handler = new MockHandler();
        logger.addHandler(handler);
        try {
            builder.build().generateARandomPassword();
        } finally {
            logger.removeHandler(handler);
            logger.setUseParentHandlers(useParentHandlers);
        }
        assertTrue("Error logging incorrect: " + handler.toString(),
                    handler.toString().contains("Failed to generate a password because excludeWords is too restrictive."));
    }
}
