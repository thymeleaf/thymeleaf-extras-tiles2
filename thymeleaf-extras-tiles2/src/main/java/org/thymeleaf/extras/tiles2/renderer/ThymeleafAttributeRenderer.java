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
package org.thymeleaf.extras.tiles2.renderer;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.jsp.context.JspTilesRequestContext;
import org.apache.tiles.renderer.impl.AbstractTypeDetectingAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.dom.DOMSelector;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.tiles2.dialect.TilesDialect;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesFragmentAttrProcessor;
import org.thymeleaf.extras.tiles2.naming.ThymeleafTilesNaming;
import org.thymeleaf.fragment.DOMSelectorFragmentSpec;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.fragment.WholeFragmentSpec;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;
import org.thymeleaf.standard.fragment.StandardFragmentSignatureNodeReferenceChecker;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafAttributeRenderer 
        extends AbstractTypeDetectingAttributeRenderer {

    
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafAttributeRenderer.class);

    
    
    private static final String SPRING3_STANDARD_DIALECT_CLASS_NAME =
            "org.thymeleaf.spring3.dialect.SpringStandardDialect";
    private static final String SPRING4_STANDARD_DIALECT_CLASS_NAME =
            "org.thymeleaf.spring4.dialect.SpringStandardDialect";


    /** Fixed an issue where the ClassLoader was being hit heavily to check to see if 
        what Spring Dialect was being used. Now check from one class instead of the Class 
        Loader
    **/
    final static Class<?> spring3StandardDialectClass = getSpring3StandardDialectClassname();
    final static Class<?> spring4StandardDialectClass = getSpring4StandardDialectClassname();
    
    public ThymeleafAttributeRenderer() {
        super();
    }

	private static Class<?> getSpring3StandardDialectClassname() {
		Class<?> springStandardDialectClass = null;
		try {
			springStandardDialectClass = Class
					.forName(SPRING3_STANDARD_DIALECT_CLASS_NAME);
		} catch (ClassNotFoundException e) {
		}
		return springStandardDialectClass;
	}
    
	private static Class<?> getSpring4StandardDialectClassname() {
		Class<?> springStandardDialectClass = null;
		try {
			springStandardDialectClass = Class
					.forName(SPRING4_STANDARD_DIALECT_CLASS_NAME);
		} catch (ClassNotFoundException e) {
		}
		return springStandardDialectClass;
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
        

        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][TILES] Rendering Thymeleaf Tiles attribute with value \"{}\"", new Object[] {value});
        }
        
        
        final ServletTilesRequestContext requestContext = 
                ServletUtil.getServletRequest(tilesRequestContext);
        
        final String templateSelector = ((String) value).trim();

        final HttpServletRequest request = requestContext.getRequest();
        final HttpServletResponse response = requestContext.getResponse();

        final TemplateEngine templateEngine = 
                (TemplateEngine) request.getAttribute(ThymeleafTilesNaming.TEMPLATE_ENGINE_ATTRIBUTE_NAME);
        final IProcessingContext processingContext = 
                (IProcessingContext) request.getAttribute(ThymeleafTilesNaming.PROCESSING_CONTEXT_ATTRIBUTE_NAME);

        // This one could be null!
        final FragmentMetadata fragmentBehaviour =
                (FragmentMetadata) request.getAttribute(ThymeleafTilesNaming.FRAGMENT_METADATA_ATTRIBUTE_NAME);
        // Once retrieved, we have to remove it in order to avoid it affecting other templates
        request.removeAttribute(ThymeleafTilesNaming.FRAGMENT_METADATA_ATTRIBUTE_NAME);
                
        
        if (tilesRequestContext instanceof JspTilesRequestContext) {
            
            // If our Thymeleaf template is being executed from a JSP, we should
            // first flush the associated JspWriter (because it will be buffering
            // instead of directly writing to the response.getWriter() we will be writing.

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][TILES] Current Tiles Request Context is a JSP" +
                		"context. Flushing JspWriter to avoid fragment writing order " +
                		"problems.");
            }
            
            final JspTilesRequestContext jspTilesRequestContext = (JspTilesRequestContext) tilesRequestContext;
            final PageContext pageContext = jspTilesRequestContext.getPageContext();
            pageContext.getOut().flush();
            
        }
        
        final boolean displayOnlySelectionChildren =
                (fragmentBehaviour != null? fragmentBehaviour.isDisplayOnlyChildren() : false);
        
        final TilesFragment fragment =
                computeTemplateSelector(templateEngine, processingContext, templateSelector);

        final String templateName = fragment.getTemplateName();
        IFragmentSpec fragmentSpec = fragment.getFragmentSpec();

        if (displayOnlySelectionChildren) {
            fragmentSpec = new FragmentSpecRootRemovingWrapper(fragmentSpec);
        }

        templateEngine.process(templateName, processingContext, fragmentSpec, response.getWriter());


        if (logger.isDebugEnabled()) {
            logger.debug("[THYMELEAF][TILES] Rendered Thymeleaf Tiles attribute with value \"{}\"", new Object[] {value});
        }
        
        
    }
    
    

    private static TilesFragment computeTemplateSelector(final TemplateEngine templateEngine,
            final IProcessingContext processingContext, final String templateSelector) {

        if (!templateEngine.isInitialized()) { 
            templateEngine.initialize();
        }
        
        if (!isStandardDialectPresent(templateEngine)) {
            return computeNonStandardFragment(templateEngine, templateSelector);
        }
        
        final Configuration configuration = templateEngine.getConfiguration();

        final String tilesDialectPrefix = getTilesDialectPrefix(templateEngine);

        final StandardFragment standardFragment =
                StandardFragmentProcessor.computeStandardFragmentSpec(configuration, processingContext,
                    templateSelector, tilesDialectPrefix, TilesFragmentAttrProcessor.ATTR_NAME);

        return new TilesFragment(standardFragment);
        
    }

    /** Fixed an issue where the ClassLoader was being hit heavily to check to see if 
        what Spring Dialect was being used. Now check from one class instead of the Class 
        Loader
    **/
    private static boolean isStandardDialectPresent(final TemplateEngine templateEngine) {
    
        if (spring3StandardDialectClass != null)
            if (isDialectPresent(templateEngine, spring3StandardDialectClass)) {
                return true;
        }

	if (spring4StandardDialectClass != null)
	    if (isDialectPresent(templateEngine, spring4StandardDialectClass)) {
                return true;
        }

        return isDialectPresent(templateEngine, StandardDialect.class);
        
    }
 

    /** Beed to clean this up to improve performance
    **/
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
    private static TilesFragment computeNonStandardFragment(
            final TemplateEngine templateEngine, final String templateSelector) {

        // Note template names in Tiles2 cannot be null (no way of executing the fragment on the "current" template)

        if (templateSelector.trim().endsWith(")")) {
            // It seems that parameters have been specified, which is forbidden if the Standard Dialect is not enabled.
            throw new TemplateProcessingException(
                    "Cannot process template selector \"" + templateSelector + "\": The Thymeleaf Standard Dialect " +
                    "has not been enabled, and therefore no parameters can be allowed for fragments.");
        }

        final int separatorPos = templateSelector.indexOf("::");
        
        if (separatorPos < 0) {
            return new TilesFragment(templateSelector, WholeFragmentSpec.INSTANCE);
        }
        
        final String templateName = templateSelector.substring(0, separatorPos).trim();
        String fragmentSelector = templateSelector.substring(separatorPos + 2).trim();

        if (fragmentSelector.length() > 3 &&
                fragmentSelector.charAt(0) == '[' && fragmentSelector.charAt(fragmentSelector.length() - 1) == ']' &&
                fragmentSelector.charAt(fragmentSelector.length() - 2) != '\'') {
            // For legacy compatibility reasons, we allow fragment DOM Selector expressions to be specified
            // between brackets. Just remove them.
            fragmentSelector = fragmentSelector.substring(1, fragmentSelector.length() - 1);
        }

        final String tilesDialectPrefix = getTilesDialectPrefix(templateEngine);

        final DOMSelector.INodeReferenceChecker nodeReferenceChecker =
                new StandardFragmentSignatureNodeReferenceChecker(
                        templateEngine.getConfiguration(), tilesDialectPrefix, TilesFragmentAttrProcessor.ATTR_NAME);

        final IFragmentSpec fragmentSpec = new DOMSelectorFragmentSpec(fragmentSelector, nodeReferenceChecker);

        return new TilesFragment(templateName, fragmentSpec);

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
