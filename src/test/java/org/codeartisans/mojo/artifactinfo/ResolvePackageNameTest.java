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
 *
 * @author Mirko Friedenhagen
 */
public class ResolvePackageNameTest extends TestCase {
    
    /**
     * Test of resolve method, of class ResolvePackageName.
     */
    public void testResolvePackageGiven() {
        String expResult = "org.example";
        ResolvePackageName instance = new ResolvePackageName(expResult, "doesnotmatter");        
        String result = instance.resolve();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of resolve method, of class ResolvePackageName.
     */
    public void testResolveInvalidPackageGiven() {
        ResolvePackageName instance = new ResolvePackageName("org_example", "doesnotmatter");        
        try {
            instance.resolve();
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }        
    }
    
    /**
     * Test of resolve method, of class ResolvePackageName.
     */
    public void testResolveNoPackageGiven() {
        ResolvePackageName instance = new ResolvePackageName(null, "org.example.foo");        
        String expResult = "org.example.foo";
        String result = instance.resolve();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of resolve method, of class ResolvePackageName.
     */
    public void testResolveNoPackageGivenWithLeadingAndTrailingDot() {
        ResolvePackageName instance = new ResolvePackageName(null, ".org.example.foo.");        
        String expResult = "org.example.foo";
        String result = instance.resolve();
        assertEquals(expResult, result);
    }
}
