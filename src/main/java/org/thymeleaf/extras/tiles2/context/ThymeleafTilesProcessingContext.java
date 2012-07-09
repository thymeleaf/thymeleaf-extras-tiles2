/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.tiles2.context;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.context.AbstractProcessingContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesProcessingContext extends AbstractProcessingContext {

    
    
    public ThymeleafTilesProcessingContext(final IWebContext context) {
        super(context);
    }


    public ThymeleafTilesProcessingContext(final IWebContext context,
            final Map<String, Object> localVariables) {
        super(context, localVariables);
    }
   
    
    public ThymeleafTilesProcessingContext(final IWebContext context,
            final Map<String, Object> localVariables, final Object selectionTarget,
            final boolean selectionTargetSet) {
        super(context, localVariables, selectionTarget, selectionTargetSet);
    }
    
    
    public ThymeleafTilesProcessingContext(final IProcessingContext processingContext) {
        
        super((processingContext == null? null : processingContext.getContext()),
                (processingContext == null? null : processingContext.getLocalVariables()),
                (processingContext == null? null : processingContext.getSelectionTarget()),
                (processingContext == null? false : processingContext.hasSelectionTarget()));
        
        Validate.notNull(processingContext, "Processing context cannot be null");
        Validate.isTrue(processingContext != null && (processingContext.getContext() instanceof IWebContext), 
                "Cannot create " + ThymeleafTilesProcessingContext.class.getSimpleName() + ": " + 
                "The specified context does not implement " + IWebContext.class.getName());
        
    }


    

    @SuppressWarnings("unchecked")
    public ThymeleafTilesProcessingContext refresh(
            final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
            final ServletContext servletContext) {
        
        final IContext context = getContext();
        final VariablesMap<String,Object> variables = context.getVariables();
        final Enumeration<String> attributeNames = httpServletRequest.getAttributeNames();
        
        while (attributeNames.hasMoreElements()) {
            final String name = attributeNames.nextElement();
            if (!isInternalRequestAttribute(name)) {
                if (!variables.containsKey(name) && !hasLocalVariable(name)) {
                    return addMissingRequestVariables(httpServletRequest, httpServletResponse, servletContext);
                }
            }
        }
        
        return this;
        
    }
    
    
    
    private static boolean isInternalRequestAttribute(final String name) {
        return (name != null && 
                (name.startsWith("org.apache.tiles") ||
                 name.startsWith("javax.servlet.jsp") ||
                 name.startsWith("%%THYMELEAF_")));
    }
    

    
    
    @SuppressWarnings("unchecked")
    private ThymeleafTilesProcessingContext addMissingRequestVariables(
            final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
            final ServletContext servletContext) {
        
        final IContext context = getContext();
        final Locale locale = context.getLocale();
        
        final Map<String,Object> variables = new HashMap<String,Object>(context.getVariables());
        
        final Enumeration<String> attributeNames = httpServletRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            final String name = attributeNames.nextElement();
            if (!isInternalRequestAttribute(name)) {
                if (!variables.containsKey(name) && !hasLocalVariable(name)) {
                    variables.put(name, httpServletRequest.getAttribute(name));
                }
            }
        }
        
        final IWebContext newContext = 
                new WebContext(httpServletRequest, httpServletResponse, servletContext, locale, variables);
        
        return new ThymeleafTilesProcessingContext(
                newContext, getLocalVariables(), getSelectionTarget(), hasSelectionTarget());
        
    }


    
    
    
    @Override
    public IWebContext getContext() {
        return (IWebContext) super.getContext();
    }
    

    
}
