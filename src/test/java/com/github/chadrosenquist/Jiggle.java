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

import java.util.Random;
import java.util.logging.Logger;

/**
 * Used to test multithreading.  Makes the thread randomly
 * sleep, yield control, or do nothing.
 * 
 * @author Chad Rosenquist
 *
 */
public class Jiggle {
    private static final Logger LOGGER = Logger.getLogger(Jiggle.class.getName());
    
    private final Random random;
    
    /**
     * Default constructor.
     */
    public Jiggle() {
        this.random = new Random();
    }
    
    /**
     * Constructor.  Allows the tester to override Random, in case the
     * tester wants to change the behavior.
     * 
     * @param random Random number generator.
     */
    public Jiggle(Random random) {
        this.random = random;
    }
    
    /**
     * Picks a random number, 1 through 10.
     * 1 = Sleep for one millisecond.
     * 2 = Yield the thread.
     * 3-10 = Do nothing.
     */
    public void jiggle() {
        try {
            int number = random.nextInt(10);
            if (number == 0)
                Thread.sleep(1);
            else if (number == 1)
                Thread.yield();
        }
        catch (final InterruptedException ex) {
            LOGGER.severe("Jiggle.jiggle() failed to generate a random number.  Continuing anyways.  " + ex.toString());
        }
    }
}