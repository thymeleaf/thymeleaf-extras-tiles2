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
package org.thymeleaf.extras.tiles2.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.AbstractTypeDetectingAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ProcessingContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContext;
import org.thymeleaf.extras.tiles2.dialect.TilesDialect;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesFragmentAttrProcessor;
import org.thymeleaf.extras.tiles2.naming.ThymeleafRequestAttributeNaming;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.ElementAndAttributeNameFragmentSpec;
import org.thymeleaf.fragment.FragmentAndTarget;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafAttributeRenderer 
        extends AbstractTypeDetectingAttributeRenderer {

    
    
    private static final String SPRING_STANDARD_DIALECT_CLASS_NAME = "org.thymeleaf.spring3.dialect.SpringStandardDialect";

    
    
    
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
            IProcessingContext processingContext = 
                    (IProcessingContext) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.PROCESSING_CONTEXT);

            if (templateEngine == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " a TemplateEngine object must be present as a request attribute with name \"" + 
                        ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE + "\". Make sure the sequence of " +
                		"request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            if (processingContext == null) {
                final IContext context = 
                        (IContext) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.CONTEXT);
                if (context == null) {
                    throw new InvalidTemplateException(
                            "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                            " either an IProcessingContext object must be present as a request attribute with name \"" + 
                            ThymeleafRequestAttributeNaming.PROCESSING_CONTEXT + "\" or an IContext object must " +
                    		"be present as a request attribute with name \"" + ThymeleafRequestAttributeNaming.CONTEXT + 
                    		"\". Make sure the sequence of request items you use when calling Tiles is: " +
                            "(TemplateEngine, [IProcessingContext|IContext], HttpServletRequest, HttpServletResponse, Writer)");
                }
                processingContext = new ProcessingContext(context);
            }
            if (httpServletResponse == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " an HttpServletResponse object must be specified (and in this request context, it is null). " +
                        "Make sure the sequence of request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            
            requestContext =
                    new ThymeleafTilesRequestContext(
                            tilesRequestContext, templateEngine, processingContext, tilesRequestContext.getWriter());
            
        } else {
            
            // Request context is of Thymeleaf's own type
            requestContext = (ThymeleafTilesRequestContext) tilesRequestContext;
            
        }
        
        final String templateSelector = (String) value;
        
        final TemplateEngine templateEngine = requestContext.getTemplateEngine();
        final IProcessingContext processingContext = requestContext.getProcessingContext();
        final Writer writer = requestContext.getWriter();

        final FragmentAndTarget fragmentAndTarget = 
                computeTemplateSelector(templateEngine, processingContext, templateSelector);

        final String templateName = fragmentAndTarget.getTemplateName();
        final IFragmentSpec fragmentSpec = fragmentAndTarget.getFragmentSpec();


        templateEngine.process(templateName, processingContext, fragmentSpec, writer);
        
    }
    
    
    
    private static FragmentAndTarget computeTemplateSelector(final TemplateEngine templateEngine, 
            final IProcessingContext processingContext, final String templateSelector) {

        if (!templateEngine.isInitialized()) { 
            templateEngine.initialize();
        }
        
        if (!isStandardDialectPresent(templateEngine)) {
            return computeNonStandardFragment(templateEngine, templateSelector);
        }
        
        final Configuration configuration = templateEngine.getConfiguration();
        
        return StandardFragmentProcessor.computeStandardFragmentSpec(configuration, processingContext, 
                templateSelector, null, getFragmentAttributeName(templateEngine));
        
    }

    
    
    
    private static boolean isStandardDialectPresent(final TemplateEngine templateEngine) {
        
        try {
            final Class<?> springStandardDialectClass = 
                    Class.forName(SPRING_STANDARD_DIALECT_CLASS_NAME);
            return isDialectPresent(templateEngine, springStandardDialectClass);
        } catch (final ClassNotFoundException e) {
            // nothing to do, just do other checks
        }
        
        return isDialectPresent(templateEngine, StandardDialect.class);
        
    }
 

    
    private static boolean isDialectPresent(final TemplateEngine templateEngine, final Class<?> dialectClass) {
        for (final IDialect dialect : templateEngine.getDialects()) {
            if (dialectClass.isAssignableFrom(dialect.getClass())) {
                return true;
            }
        }
        return false;
    }

    
    
    /*
     * Syntax is like the standard one, only without expression evaluation: 
     *     TEMPLATE_NAME :: FRAGMENT_NAME
     *     TEMPLATE_NAME :: [DOM_SELECTOR]
     */
    private static FragmentAndTarget computeNonStandardFragment(
            final TemplateEngine templateEngine, final String templateSelector) {
        
        final int separatorPos = templateSelector.indexOf("::");
        
        if (separatorPos < 0) {
            return new FragmentAndTarget(templateSelector, WholeFragmentSpec.INSTANCE);
        }
        
        final String templateName = templateSelector.substring(0, separatorPos).trim();
        final String fragmentSelector = templateSelector.substring(separatorPos + 2).trim();
        
        if (fragmentSelector.length() > 2 && fragmentSelector.startsWith("[") && fragmentSelector.endsWith("]")) {
            final String domSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1);
            return new FragmentAndTarget(templateName, new DOMSelectorFragmentSpec(domSelector));
        }
        
        return new FragmentAndTarget(templateName, 
                new ElementAndAttributeNameFragmentSpec(null, getFragmentAttributeName(templateEngine), fragmentSelector));
        
    }
    
    
    
    private static String getTilesDialectPrefix(final TemplateEngine templateEngine) {
        
        for (final Map.Entry<String,IDialect> dialectByPrefix : templateEngine.getDialectsByPrefix().entrySet()) {
            final IDialect dialect = dialectByPrefix.getValue();
            if (TilesDialect.class.isAssignableFrom(dialect.getClass())) {
                return dialectByPrefix.getKey();
            }
        }
        
        throw new ConfigurationException(
                "Tiles dialect has not been found. In order to use Apache Tiles with Thymeleaf, you should configure " +
                "the " + TilesDialect.class.getName() + " dialect at your Template Engine");
        
    }
    
    
    private static String getFragmentAttributeName(final TemplateEngine templateEngine) {
        // In most cases: "tiles:fragment"
        return getTilesDialectPrefix(templateEngine) + ":" + TilesFragmentAttrProcessor.ATTR_NAME;
    }




    
    public boolean isRenderable(final Object value, final Attribute attribute,
            final TilesRequestContext request) {
        // Thyemeleaf cannot give a real answer to this as resources are made available to
        // the processing core only by means of actually reading them (the type of resource is
        // hidden by the ITemplateResolver / IResourceResolver), and it could be that
        // there is no way of knowing if a resource is available or not before actually
        // reading it.
        //
        // So this attribute renderer, when chained, should be always put at the end of the chain.
        return true;
    }





    
    
}
