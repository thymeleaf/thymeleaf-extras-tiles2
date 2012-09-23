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
import org.apache.tiles.template.GetAsStringModel;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Macro;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractChildrenModifierAttrProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class TilesStringAttrProcessor
        extends AbstractChildrenModifierAttrProcessor {
    

    public static final String ATTR_NAME = "string";
    public static final int PRECEDENCE = 100;
    
    
    // Initialized in the same way it is done in GetAsStringTag (JSP) and
    // GetAsStringDirective (Velocity)
    private final GetAsStringModel model = 
            new GetAsStringModel(new DefaultAttributeResolver());
    
    
    
    public TilesStringAttrProcessor() {
        super(ATTR_NAME);
    }

    
    

    @Override
    public int getPrecedence() {
        return PRECEDENCE;
    }

    
    

    @Override
    protected List<Node> getModifiedChildren(final Arguments arguments,
            final Element element, String attributeName) {

        final String attributeValue = element.getAttributeValue(attributeName);
        
        final IContext context = arguments.getContext();
        if (!(context instanceof IWebContext)) {
            throw new ConfigurationException(
                    "Thymeleaf execution context is not a web context (implementation of " +
                    IWebContext.class.getName() + ". Tiles integration can only be used in " +
            		"web environements.");
        }
        final IWebContext webContext = (IWebContext) context;
        
        final HttpServletRequest request = webContext.getHttpServletRequest();
        final HttpServletResponse response = webContext.getHttpServletResponse();
        final ServletContext servletContext = webContext.getServletContext();
        
        final TilesContainer tilesContainer = ServletUtil.getContainer(servletContext);

        final boolean ignore = false;
        final String preparer = null;
        final String role = null;
        final Object defaultValue = null;
        final String defaultValueRole = null;
        final String defaultValueType = null;
        final String name = attributeValue;
        final Attribute value = null;
        
        final TemplateEngine templateEngine = arguments.getTemplateEngine();
        final StringWriter writer = new StringWriter();
        
        try {
            this.model.execute(
                    tilesContainer, writer, ignore, preparer, 
                    role, defaultValue, defaultValueRole, 
                    defaultValueType, name, value,
                    templateEngine, arguments,
                    request, response, writer);
        } catch (final IOException e)  {
            throw new TemplateProcessingException(
                    "Error while processing Tiles attribute \"" + name + "\"", e);
        }
        
        final String templateResult = writer.toString();
        
        final Macro macroNode = new Macro(templateResult);
        
        return Collections.singletonList((Node)macroNode);
        
    }



    
    
    
}
