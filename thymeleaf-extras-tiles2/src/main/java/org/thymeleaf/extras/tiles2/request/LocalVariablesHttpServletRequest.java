/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.extras.tiles2.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class LocalVariablesHttpServletRequest extends HttpServletRequestWrapper {

    
    private final Map<String,Object> localVariables;
    
    
    public LocalVariablesHttpServletRequest(final HttpServletRequest request, final Map<String,Object> localVariables) {
        super(request);
        this.localVariables = localVariables;
    }


    @Override
    public Object getAttribute(final String name) {
        if (this.localVariables != null && this.localVariables.containsKey(name)) {
            return this.localVariables.get(name);
        }
        return super.getAttribute(name);
    }


    @Override
    @SuppressWarnings("rawtypes")
    public Enumeration getAttributeNames() {
        final Enumeration attributeNamesEnum = super.getAttributeNames();
        final List<String> attributeNames = new ArrayList<String>();
        while (attributeNamesEnum.hasMoreElements()) {
            attributeNames.add((String)attributeNamesEnum.nextElement());
        }
        attributeNames.addAll(this.localVariables.keySet());
        return Collections.enumeration(attributeNames);
    }
    
    
}
