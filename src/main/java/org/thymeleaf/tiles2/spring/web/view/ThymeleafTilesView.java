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

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletTilesApplicationContext;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.tiles2.spring.web.configurer.ThymeleafTilesConfigurer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafTilesView extends AbstractUrlBasedView {
    
    
    
    
    public ThymeleafTilesView() {
        super();
    }

    
    


    // TODO Shouldn't we implement this method better?
    @Override
    public boolean checkResource(final Locale locale) throws Exception {
        TilesContainer container = ServletUtil.getContainer(getServletContext());
        if (!(container instanceof BasicTilesContainer)) {
            // Cannot check properly - let's assume it's there.
            return true;
        }
        BasicTilesContainer basicContainer = (BasicTilesContainer) container;
        TilesApplicationContext appContext = new ServletTilesApplicationContext(getServletContext());
        TilesRequestContext requestContext = new ServletTilesRequestContext(appContext, null, null) {
            @Override
            public Locale getRequestLocale() {
                return locale;
            }
        };
        return (basicContainer.getDefinitionsFactory().getDefinition(getUrl(), requestContext) != null);
    }
    

    
    
    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        ServletContext servletContext = getServletContext();
        final RequestContext requestContext = 
                new RequestContext(request, response, servletContext, model);
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
        

        TilesContainer container = ServletUtil.getContainer(servletContext);
        if (container == null) {
            throw new ServletException(
                    "Tiles container is not initialized. " +
                    "Have you added a " + ThymeleafTilesConfigurer.class.getSimpleName() + " to " +
            		"your web application context?");
        }

        exposeModelAsRequestAttributes(model, request);
        JstlUtils.exposeLocalizationContext(new RequestContext(request, servletContext));

        // TODO Maybe this means we should be using a specific view resolver?
        final TemplateEngine templateEngine = TemplateEngine.threadTemplateEngine();
        final Locale locale = RequestContextUtils.getLocale(request);
        
        final IWebContext context = 
                new SpringWebContext(request, response, servletContext , 
                        locale, model, getApplicationContext());
        
        
        container.render(getUrl(), templateEngine, context, request, response, response.getWriter());
        
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
