/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.tiles2.dialect.processor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;
import org.apache.tiles.template.DefaultAttributeResolver;
import org.apache.tiles.template.InsertAttributeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.DialectAwareProcessingContext;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.tiles2.renderer.FragmentMetadata;
import org.thymeleaf.extras.tiles2.request.LocalVariablesHttpServletRequest;
import org.thymeleaf.processor.attr.AbstractChildrenModifierAttrProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public abstract class AbstractTilesFragmentHandlingAttrProcessor
        extends AbstractChildrenModifierAttrProcessor {

    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    
    // Initialized in the same way it is done in InsertAttributeTag (JSP) and
    // InsertAttributeDirective (Velocity)
    private final InsertAttributeModel model = 
            new InsertAttributeModel(new DefaultAttributeResolver());
    
    
    
    public AbstractTilesFragmentHandlingAttrProcessor(final String attributeName) {
        super(attributeName);
    }


    
    

    @Override
    protected final List<Node> getModifiedChildren(final Arguments arguments,
            final Element element, String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        final boolean replaceHostElement = getReplaceHostElement(arguments, element, attributeName);

        final IContext context = arguments.getContext();
        if (!(context instanceof IWebContext)) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                    IWebContext.class.getName() + ". Tiles integration can only be used in " +
            		"web environements.");
        }
        final IWebContext webContext = (IWebContext) context;
        
        final IProcessingContext processingContext = new DialectAwareProcessingContext(arguments);
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final HttpServletResponse response = webContext.getHttpServletResponse();
        final ServletContext servletContext = webContext.getServletContext();
        
        /*
         * A special implementation of HttpServletRequest will make sure a (potential) JSP
         * fragment insertion will see the currently defined local variables, without just
         * adding them as attributes to the request, which would mean they would be available
         * everywhere in the page (and local variables in Thymeleaf are scoped to the element
         * they are defined in). 
         */
        final LocalVariablesHttpServletRequest localVariablesHttpServletRequest =
                new LocalVariablesHttpServletRequest(request, processingContext.getLocalVariables());
        
        
        /*
         * We assume the TilesContainer has been declared by a factory and hooked to the
         * ServletContext in the usual Tiles way. 
         */
        final TilesContainer tilesContainer = ServletUtil.getContainer(servletContext);

        
        /*
         * Initialize model execution parameters. Many parameters cannot be given a value
         * in our dialect's attributes, so we will just assign default parameters to them.
         */
        final boolean ignore = false;
        final String preparer = null;
        final String role = null;
        final Object defaultValue = null;
        final String defaultValueRole = null;
        final String defaultValueType = null;
        final String name = attributeValue;
        final Attribute value = null;
        
        

        final TemplateEngine templateEngine = arguments.getTemplateEngine();
        
        /*
         * We use a StringWriter in order to avoid anyone writing directly to the
         * response writer (something that would mess our fragments).
         */
        final StringWriter writer = new StringWriter();
        
        
        /*
         * Create the fragment metadata object that will determine whether
         * the rendered fragment will include the containing markup element 
         * (substituteby) or not (include).
         */
        final FragmentMetadata fragmentMetadata = new FragmentMetadata(name);
        fragmentMetadata.setDisplayOnlyChildren(!replaceHostElement);
        
        
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Executing Tiles Model of class {}" +
            		"for attribute \"{}\" with value \"{}\"",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(),
                    this.model.getClass().getName(), attributeName, attributeValue});
        }
        
        /*
         * Actual execution of the Tiles model. This will end up triggering
         * the ThymeleafAttributeRenderer.
         */
        try {
            this.model.execute(
                    tilesContainer, ignore, preparer, 
                    role, defaultValue, defaultValueRole, 
                    defaultValueType, name, value,
                    templateEngine, processingContext,
                    localVariablesHttpServletRequest, response, writer,
                    fragmentMetadata);
        } catch (final IOException e)  {
            throw new TemplateProcessingException(
                    "Error while processing Tiles attribute \"" + name + "\"", e);
        }
        
        final String templateResult = writer.toString();
        
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("[THYMELEAF][{}][{}] Correctly executed Tiles Model of class {}" +
                    "for attribute \"{}\" with value \"{}\", result is:\n{}",
                    new Object[] {TemplateEngine.threadIndex(), arguments.getTemplateName(),
                    this.model.getClass().getName(), attributeName, attributeValue, templateResult});
        }
        
        final Macro macroNode = new Macro(templateResult);
        
        return Collections.singletonList((Node)macroNode);
        
    }



    
    
    
}
