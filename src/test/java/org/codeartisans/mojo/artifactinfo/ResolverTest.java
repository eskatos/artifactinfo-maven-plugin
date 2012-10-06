/*
 * Copyright 2012 Mirko Friedenhagen.
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

import junit.framework.TestCase;

/**
 * @author Mirko Friedenhagen
 */
public class ResolverTest extends TestCase {
    
    static class DummyResolver extends Resolver {

        public DummyResolver() {
            super("abc");
        }
        
        @Override
        String resolve() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    /**
     * Test of validatesName method, of class Resolver.
     */
    public void testValidatesName() {
        Resolver instance = new DummyResolver();
        instance.validatesName("a");
    }

    /**
     * Test of validatesName method, of class Resolver.
     */
    public void testValidatesNameInvalid() {
        Resolver instance = new DummyResolver();
        try {
            instance.validatesName("d");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Given name [d] contains unallowed char [d], allowed are [abc]", e.getMessage());
        }
    }
    
    /**
     * Test of specialTrim method, of class Resolver.
     */
    public void testSpecialTrim() {
        Resolver instance = new DummyResolver();
        String expResult = "org.example";
        String result = instance.specialTrim(expResult);
        assertEquals(expResult, result);
    }

    /**
     * Test of specialTrim method, of class Resolver.
     */
    public void testSpecialTrimWithLeadingAndTrailingDots() {
        Resolver instance = new DummyResolver();
        String expResult = "org.example";
        String result = instance.specialTrim("." + expResult + ".");
        assertEquals(expResult, result);
    }
}
