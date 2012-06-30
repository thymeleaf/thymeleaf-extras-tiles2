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
package org.thymeleaf.tiles2.renderer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.AbstractBaseAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.expression.ExpressionEvaluationContext;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;
import org.thymeleaf.tiles2.context.ThymeleafTilesRequestContext;
import org.thymeleaf.tiles2.naming.ThymeleafRequestAttributeNaming;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafAttributeRenderer extends AbstractBaseAttributeRenderer {

    
    public static final String THYMELEAF_ATTRIBUTE_TYPE = "thymeleaf";
    

    
    
    
    public ThymeleafAttributeRenderer() {
        super();
    }

    
    
    
    @Override
    public void write(final Object value, final Attribute attribute,
            final TilesRequestContext tilesRequestContext) throws IOException {

        if (value == null) {
            throw new InvalidTemplateException("Cannot render a null template");
        }
        if (!(value instanceof String)) {
            throw new InvalidTemplateException(
                    "Cannot render a template that is not a String ('" + value.getClass().getName() +"')");
        }

        // Request could be Thymeleaf's own type or maybe a type from another
        // technology, but in that case we will need some attributes in the request
        // (the template engine and the context).
        ThymeleafTilesRequestContext requestContext = null;

        if (!(tilesRequestContext instanceof ThymeleafTilesRequestContext)) {

            // If our request context is not a Thymeleaf one, could be because we are trying to render
            // a Thymeleaf attribute from a non-thymeleaf page, typically a JSP.
            final ServletTilesRequestContext servletTilesRequestContext =
                    ServletUtil.getServletRequest(tilesRequestContext);

            final HttpServletRequest httpServletRequest = servletTilesRequestContext.getRequest();
            final HttpServletResponse httpServletResponse = servletTilesRequestContext.getResponse();
            
            final TemplateEngine templateEngine = 
                    (TemplateEngine) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE);
            final IContext context = 
                    (IContext) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.CONTEXT);

            if (templateEngine == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " a TemplateEngine object must be present as a request attribute with name \"" + 
                        ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE + "\". Make sure the sequence of " +
                		"request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            if (context == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " an IContext object must be present as a request attribute with name \"" + 
                        ThymeleafRequestAttributeNaming.CONTEXT + "\". Make sure the sequence of " +
                        "request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            if (httpServletResponse == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " an HttpServletResponse object must be specified (and in this request context, it is null). " +
                        "Make sure the sequence of request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            
            requestContext =
                    new ThymeleafTilesRequestContext(tilesRequestContext, templateEngine, context, tilesRequestContext.getWriter());
            
        } else {
            
            // Request context is of Thymeleaf's own type
            requestContext = (ThymeleafTilesRequestContext) tilesRequestContext;
            
        }
        
        final String templateSelector = (String) value;
        
        final TemplateEngine templateEngine = requestContext.getTemplateEngine();
        final IContext context = requestContext.getContext();
        final Writer writer = requestContext.getWriter();

        final FragmentAndTarget fragmentAndTarget = 
                computeTemplateSelector(templateEngine, context, templateSelector);
        final String templateName = fragmentAndTarget.getTemplateName();
        final IFragmentSpec fragmentSpec = fragmentAndTarget.getFragmentSpec();

        templateEngine.process(templateName, context, fragmentSpec, writer);
        
    }
    
    
    
    private static FragmentAndTarget computeTemplateSelector(final TemplateEngine templateEngine, 
            final IContext context, final String templateSelector) {

        /*
         * TODO DETECT IF THE STANDARD DIALECTS ARE PRESENT. IF NOT, DO NOT TRY TO PARSE
         * THE SELECTOR AS A STANDARD FRAGMENT SPECIFICATION
         */
        
        if (!templateEngine.isInitialized()) { 
            templateEngine.initialize();
        }
        
        final Configuration configuration = templateEngine.getConfiguration();
        final ExpressionEvaluationContext evalContext = new ExpressionEvaluationContext(context);
        
        return StandardFragmentProcessor.computeStandardFragmentSpec(configuration, evalContext, 
                templateSelector, null, "tiles:fragment");
        
    }

        
}
