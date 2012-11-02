/*
 * Copyright 2012
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

import java.util.Locale;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Paul Merlin
 * @author Mirko Friedenhagen
 */
class ResolveClassName extends Resolver {
    
    final String givenClassName;
    
    final String artifactId;

    public ResolveClassName(final String givenClassName, final String artifactId) {
        super("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        this.givenClassName = givenClassName;
        this.artifactId = artifactId;
    }

    String resolve() {        
        if (!StringUtils.isEmpty(givenClassName)) {
            validatesName(givenClassName);
            return givenClassName;
        }
        final StringBuilder sb = new StringBuilder();
        final String nonFiltered = specialTrim(artifactId);
        String previous = new String(new char[]{nonFiltered.charAt(0)});
        sb.append(previous.toUpperCase(Locale.ENGLISH));
        for (int idx = 1; idx < nonFiltered.length(); idx++) {
            char current = nonFiltered.charAt(idx);
            if (validCharacters.indexOf((int) current) != -1) {
                if (validCharacters.contains(previous)) {
                    sb.append(current);
                } else {
                    sb.append(new String(new char[]{current}).toUpperCase(Locale.ENGLISH));
                }
            }
            previous = new String(new char[]{current});
        }
        sb.append("_ArtifactInfo");
        return sb.toString();
    }
    
}
