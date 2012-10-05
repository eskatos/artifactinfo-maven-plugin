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
class ResolvePackageName extends Resolver {
    final String givenPackageName;
    final String groupId;

    public ResolvePackageName(final String givenPackageName, final String groupId) {
        super("abcdefghijklmnopqrstuvwxyz.");
        this.givenPackageName = givenPackageName;
        this.groupId = groupId.toLowerCase(Locale.ENGLISH);
    }

    String resolve() {
        if (!StringUtils.isEmpty(givenPackageName)) {
            validatesName(givenPackageName);
            return givenPackageName;
        }
        StringBuilder sb = new StringBuilder();
        String nonFiltered = groupId;
        nonFiltered = specialTrim(nonFiltered);
        for (int idx = 0; idx < nonFiltered.length(); idx++) {
            char current = nonFiltered.charAt(idx);
            if (validCharacters.indexOf((int) current) != -1) {
                sb.append(current);
            }
        }
        return sb.toString();
    }
    
}
