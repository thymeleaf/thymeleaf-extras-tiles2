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
package org.thymeleaf.extras.tiles2.spring4.web.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.DialectAwareProcessingContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.extras.tiles2.spring4.web.configurer.ThymeleafTilesConfigurer;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.view.AbstractThymeleafView;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesView extends AbstractThymeleafView {
    
    
    
    
    public ThymeleafTilesView() {
        super();
    }


    
    
    
    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {

        final ServletContext servletContext = getServletContext();

        if (getTemplateName() == null) {
            throw new IllegalArgumentException("Property 'templateName' is required");
        }
        if (getTemplateEngine() == null) {
            throw new IllegalArgumentException("Property 'templateEngine' is required");
        }

        final TemplateEngine viewTemplateEngine = getTemplateEngine();
        
        final IProcessingContext processingContext = 
                buildContextAndPrepareResponse(viewTemplateEngine, model, request, response);
        
        final TilesContainer container = ServletUtil.getContainer(servletContext);
        if (container == null) {
            throw new ServletException(
                    "Tiles container is not initialized. " +
                    "Have you added a " + ThymeleafTilesConfigurer.class.getSimpleName() + " to " +
                    "your web application context?");
        }
        
        container.render(getTemplateName(), viewTemplateEngine, processingContext, request, response, response.getWriter());
        
    }


    
    
    protected IProcessingContext buildContextAndPrepareResponse(
            final TemplateEngine templateEngine, final Map<String, ?> model, 
            final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {

        final ServletContext servletContext = getServletContext();

        if (getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }
        
        final Map<String, Object> mergedModel = new HashMap<String, Object>();
        
        final Map<String, Object> staticVariables = this.getStaticVariables();
        if (staticVariables != null) {
            mergedModel.putAll(staticVariables);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }

        
        final RequestContext requestContext = 
                new RequestContext(request, response, servletContext, mergedModel);
        
        // For compatibility with ThymeleafView
        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        // For compatibility with AbstractTemplateView
        addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);

        
        final IWebContext context = 
                new SpringWebContext(request, response, servletContext , getLocale(), mergedModel, getApplicationContext());
        final IProcessingContext processingContext = 
                new DialectAwareProcessingContext(context, templateEngine.getDialects());
        
        final String templateContentType = getContentType();
        final Locale templateLocale = getLocale();
        final String templateCharacterEncoding = getCharacterEncoding();

        response.setLocale(templateLocale);
        if (templateContentType != null) {
            response.setContentType(templateContentType);
        } else {
            response.setContentType(DEFAULT_CONTENT_TYPE);
        }
        if (templateCharacterEncoding != null) {
            response.setCharacterEncoding(templateCharacterEncoding);
        }
        
        exposeModelAsRequestAttributes(mergedModel, request);
        JstlUtils.exposeLocalizationContext(requestContext);

        return processingContext;
        
    }
    
    


    @Override
    protected Locale getLocale() {
        return super.getLocale();
    }


    @Override
    protected void setLocale(final Locale locale) {
        super.setLocale(locale);
    }


    @Override
    protected SpringTemplateEngine getTemplateEngine() {
        return super.getTemplateEngine();
    }


    @Override
    protected void setTemplateEngine(final SpringTemplateEngine templateEngine) {
        super.setTemplateEngine(templateEngine);
    }


    @Override
    protected boolean isContentTypeSet() {
        return super.isContentTypeSet();
    }
    
    

    
    protected void exposeModelAsRequestAttributes(final Map<String, ?> model, final HttpServletRequest request) 
                throws Exception {
        
        for (final Map.Entry<String, ?> entry : model.entrySet()) {
            final String modelName = entry.getKey();
            final Object modelValue = entry.getValue();
            if (modelValue != null) {
                request.setAttribute(modelName, modelValue);
            } else {
                request.removeAttribute(modelName);
            }
        }
        
    }

    
    
}
