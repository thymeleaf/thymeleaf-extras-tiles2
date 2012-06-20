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
package org.thymeleaf.tiles2.spring.web.view;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.tiles2.TilesView;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafTilesView extends TilesView {
    
    
    
    
    public ThymeleafTilesView() {
        super();
    }

    
    
    
    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        final RequestContext requestContext = 
                new RequestContext(request, response, getServletContext(), model);
        final String specifiedRequestContextAttribute = getRequestContextAttribute();
        
        // For compatibility with ThymeleafView
        addRequestContextAsVariable(model, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // For compatibility with AbstractTemplateView
        addRequestContextAsVariable(model, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
        // For compatibility with other scenarios, as configured by the user
        if (specifiedRequestContextAttribute != null) {
            addRequestContextAsVariable(model, specifiedRequestContextAttribute, requestContext);
        }
        
        
        final String templateContentType = getContentType();

        if (templateContentType != null) {
            response.setContentType(templateContentType);
        } else {
            response.setContentType(DEFAULT_CONTENT_TYPE);
        }
        
        super.renderMergedOutputModel(model, request, response);
        
    }
    
    

    private static void addRequestContextAsVariable(
            final Map<String,Object> model, final String variableName, final RequestContext requestContext) 
            throws ServletException {
        
        if (model.containsKey(variableName)) {
            throw new ServletException(
                    "Cannot expose request context in model attribute '" + variableName +
                    "' because of an existing model object of the same name");
        }
        model.put(variableName, requestContext);
        
    }
    


    
    
}
