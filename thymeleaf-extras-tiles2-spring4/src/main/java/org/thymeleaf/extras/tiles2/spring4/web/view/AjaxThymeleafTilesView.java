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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.apache.tiles.Definition;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.servlet.context.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.js.ajax.AjaxHandler;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContextFactory;
import org.thymeleaf.extras.tiles2.spring4.web.configurer.ThymeleafTilesConfigurer;
import org.thymeleaf.spring3.view.AjaxEnabledView;


/**
 * <p>
 *   Subclass of {@link ThymeleafTilesView} adding compatibility with AJAX events in
 *   Spring JavaScript (part of Spring WebFlow). This allows this View implementation
 *   to be able to return only <i>fragments</i> of the page.
 * </p>
 * <p>
 *   These rendering of fragments is used, for example, in Spring WebFlow's &lt;render&gt;
 *   instructions (though not only).
 * </p>
 * <p>
 *   This view searches for a comma-separated list of <i>fragment names</i> in a request
 *   parameter called <kbd>fragments</kbd>.
 * </p>
 * <p>
 *   When using Tiles, fragments are considered to be <b>Tiles attribute names</b> (from a Tiles
 *   definition).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class AjaxThymeleafTilesView extends ThymeleafTilesView implements AjaxEnabledView {

    
    private static final Logger vlogger = LoggerFactory.getLogger(AjaxThymeleafTilesView.class);

    
    private static final String FRAGMENTS_PARAM = "fragments";
    
    private final ThymeleafTilesRequestContextFactory tilesRequestContextFactory = new ThymeleafTilesRequestContextFactory();

    private AjaxHandler ajaxHandler = null;



    
    public AjaxThymeleafTilesView() {
        super();
    }
    
    


    public AjaxHandler getAjaxHandler() {
        return this.ajaxHandler;
    }

    
    public void setAjaxHandler(final AjaxHandler ajaxHandler) {
        this.ajaxHandler = ajaxHandler;
    }




    @Override
    public void render(
            final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response) 
            throws Exception {

        final AjaxHandler templateAjaxHandler = getAjaxHandler();
        
        if (templateAjaxHandler == null) {
            throw new ConfigurationException("[THYMELEAF] AJAX Handler set into " +
                    AjaxThymeleafTilesView.class.getSimpleName() + " instance for template " +
                    getTemplateName() + " is null.");
        }
        
        if (templateAjaxHandler.isAjaxRequest(request, response)) {

            final String[] fragmentsToRender = getRenderFragments(model, request, response);
            if (fragmentsToRender.length == 0) {
                vlogger.warn("[THYMELEAF] An Ajax request was detected, but no fragments were specified to be re-rendered.  "
                        + "Falling back to full page render.  This can cause unpredictable results when processing "
                        + "the ajax response on the client.");
                super.render(model, request, response);
                return;
            }

            final ServletContext servletContext = getServletContext();

            if (getTemplateEngine() == null) {
                throw new IllegalArgumentException("Property 'templateEngine' is required");
            }

            if (getTemplateName() == null) {
                throw new IllegalArgumentException("Property 'templateName' is required");
            }

            final TemplateEngine viewTemplateEngine = getTemplateEngine();
            
            final IProcessingContext processingContext = 
                    buildContextAndPrepareResponse(viewTemplateEngine, model, request, response);
            
            final BasicTilesContainer container = (BasicTilesContainer) ServletUtil.getContainer(servletContext);
            if (container == null) {
                throw new ServletException(
                        "Tiles container is not initialized. " +
                        "Have you added a " + ThymeleafTilesConfigurer.class.getSimpleName() + " to " +
                        "your web application context?");
            }


            /*
             * Extract only the specified fragments as Tiles "attributes"
             */
            
            final TilesRequestContext tilesRequestContext = 
                    this.tilesRequestContextFactory.createRequestContext(
                            container.getApplicationContext(), 
                            viewTemplateEngine, processingContext, request, response, response.getWriter());
            
            final Definition compositeDefinition = 
                    container.getDefinitionsFactory().getDefinition(getTemplateName(),tilesRequestContext);
            
            final Map<String,Attribute> flattenedAttributeMap = new HashMap<String,Attribute>();
            flattenAttributeMap(
                    container, tilesRequestContext, flattenedAttributeMap, compositeDefinition, request, response);
            addRuntimeAttributes(container, flattenedAttributeMap, request, response);

            if (fragmentsToRender.length > 1) {
                // Just in case any fragment is a JSPs
                request.setAttribute(ServletUtil.FORCE_INCLUDE_ATTRIBUTE_NAME, Boolean.TRUE);
            }

            for (int i = 0; i < fragmentsToRender.length; i++) {
                
                Attribute attributeToRender = flattenedAttributeMap.get(fragmentsToRender[i]);

                if (attributeToRender == null) {
                    throw new ServletException("No tiles attribute with a name of '" + fragmentsToRender[i]
                            + "' could be found for the current view: " + this);
                }

                container.startContext(request, response).inheritCascadedAttributes(compositeDefinition);
                container.render(attributeToRender, request, response);
                container.endContext(request, response);
                
            }
            
        } else {
            
            super.render(model, request, response);
            
        }
        
    }
    
    
    
    

    @SuppressWarnings({ "rawtypes", "unused" })
    protected String[] getRenderFragments(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) {
        final String fragmentsParam = request.getParameter(FRAGMENTS_PARAM);
        final String[] renderFragments = StringUtils.commaDelimitedListToStringArray(fragmentsParam);
        return StringUtils.trimArrayElements(renderFragments);
    }
    

    

    /**
     * <p>
     *   Iterate over all attributes in the given Tiles definition. Every attribute in the defintion
     *   is added to the map, and attributes which are definitions themselves will be recursively
     *   processed.
     * </p>
     * 
     * @param container the TilesContainer
     * @param requestContext the TilesRequestContext
     * @param resultMap the output Map where attributes of interest are added to.
     * @param compositeDefinition the definition to search for attributes of interest.
     * @param request the servlet request
     * @param response the servlet response
     */
    protected void flattenAttributeMap(
            final BasicTilesContainer container, final TilesRequestContext requestContext,
            final Map<String,Attribute> resultMap, final Definition compositeDefinition, 
            final HttpServletRequest request, final HttpServletResponse response) {
        
        final Iterator<String> iterator = compositeDefinition.getAttributeNames();
        
        while (iterator.hasNext()) {
            
            final String attributeName = iterator.next();
            final Attribute attribute = compositeDefinition.getAttribute(attributeName);
            
            if (attribute.getValue() == null || !(attribute.getValue() instanceof String)) {
                continue;
            }
         
            resultMap.put(attributeName, attribute);
            
            final String value = attribute.getValue().toString();
            
            if (container.isValidDefinition(value, new Object[] { request, response })) {
                // It is a nested definition, so we have to nest the attribute definitions
                final Definition nestedDefinition = container.getDefinitionsFactory().getDefinition(value, requestContext);
                flattenAttributeMap(container, requestContext, resultMap, nestedDefinition, request, response);
            }
            
        }
    }

    
    
    /**
     * <p>
     *   Iterate over dynamically added Tiles attributes (see "Runtime Composition" in the Tiles documentation) and add
     *   them to the output Map passed as input.
     * </p>
     * 
     * @param container the Tiles container
     * @param resultMap the output Map where attributes of interest are added to.
     * @param request the Servlet request
     * @param response the Servlet response
     */
    protected void addRuntimeAttributes(
            final BasicTilesContainer container, final Map<String,Attribute> resultMap, 
            final HttpServletRequest request, final HttpServletResponse response) {

        /*
         *   Copied from {@link org.springframework.js.ajax.tiles2.AjaxTilesView}#flattenAttributeMap(BasicTilesContainer, TilesRequestContext, Map, Definition, HttpServletRequest, HttpServletResponse).
         *   Original authors of {@link org.springframework.js.ajax.tiles2.AjaxTilesView} are
         *   Jeremy Grelle and David Winterfeldt.
         */
        
        final AttributeContext attributeContext = container.getAttributeContext(new Object[] { request, response });
        final Set<String> attributeNames = new HashSet<String>();
        if (attributeContext.getLocalAttributeNames() != null) {
            attributeNames.addAll(attributeContext.getLocalAttributeNames());
        }
        if (attributeContext.getCascadedAttributeNames() != null) {
            attributeNames.addAll(attributeContext.getCascadedAttributeNames());
        }
        final Iterator<String> iterator = attributeNames.iterator();
        while (iterator.hasNext()) {
            final String attributeName = iterator.next();
            resultMap.put(attributeName, attributeContext.getAttribute(attributeName));
        }
        
    }
    
    
}
