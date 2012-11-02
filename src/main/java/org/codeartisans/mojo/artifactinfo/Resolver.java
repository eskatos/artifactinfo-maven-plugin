/*
 * Copyright 2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codeartisans.mojo.artifactinfo;

/**
 * Abstract Resolver class with helper functions for validation.
 * 
 * @author Paul Merlin
 * @author Mirko Friedenhagen
 */
abstract class Resolver {
    final String validCharacters;

    Resolver(final String validCharacters) {
        this.validCharacters = validCharacters;
    }

    void validatesName(String name) {
        for (int idx = 0; idx < name.length(); idx++) {
            char current = name.charAt(idx);
            if (validCharacters.indexOf((int) current) == -1) {
                throw new IllegalArgumentException("Given name [" + name + "] contains unallowed char [" + current + "], allowed are [" + validCharacters + ']');
            }
        }
    }

    /**
     * Trim and remove trailing dots.
     */
    String specialTrim(String str) {
        str = str.trim();
        if (str.startsWith(".")) {
            str = str.substring(1);
        }
        if (str.endsWith(".")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    abstract String resolve();
    
}
