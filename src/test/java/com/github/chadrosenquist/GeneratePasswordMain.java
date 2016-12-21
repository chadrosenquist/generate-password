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

/**
 * Use this class for manual testing.
 * Run it and see the randomly generated password on the console.
 * 
 * @author Chad Rosenquist
 *
 */
public class GeneratePasswordMain {

    public static void main(String[] args) {
        final GeneratePassword.Builder builder = new GeneratePassword.Builder();
        builder.length(12);
        builder.minUpperCase(1);
        builder.minLowerCase(1);
        builder.minDigits(2);
        builder.minSpecialChars(1);
        builder.specialChars("!@#$%^&*");
        final GeneratePassword generatePassword = builder.build();
        System.out.println(generatePassword.generateARandomPassword());
    }

}
