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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.tiles.Attribute;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.renderer.impl.AbstractTypeDetectingAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.extras.tiles2.naming.ThymeleafTilesNaming;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class MetadataCleaningAttributeRendererWrapper 
        extends AbstractTypeDetectingAttributeRenderer {

    
    private static final Logger logger = LoggerFactory.getLogger(MetadataCleaningAttributeRendererWrapper.class);
    
    private final AbstractTypeDetectingAttributeRenderer renderer;
    

    
    public MetadataCleaningAttributeRendererWrapper(
            final AbstractTypeDetectingAttributeRenderer renderer) {
        super();
        Validate.notNull(renderer, "Wrapped renderer cannot be null");
        this.renderer = renderer;
    }


    
    




    @Override
    public void render(final Attribute attribute, final TilesRequestContext tilesRequestContext)
            throws IOException {

        
        final Object value = attribute.getValue();

        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][TILES] Executing wrapped renderer of class {} for attribute with " +
                    "value \"{}\"", new Object[] {this.renderer.getClass().getName(), value});
        }

        
        /*
         * Get the request. We need to extract the FragmentBehaviour object from it.
         */
        final ServletTilesRequestContext requestContext = 
                ServletUtil.getServletRequest(tilesRequestContext);
        final HttpServletRequest request = requestContext.getRequest();

        
        /*
         * For actually processing our renderer, we will need to make sure that
         * no fragmentBehaviour objects are present in the request when we execute a
         * template that is not a Thymeleaf one.
         */
        
        final FragmentMetadata fragmentBehaviour = 
                (FragmentMetadata) request.getAttribute(ThymeleafTilesNaming.FRAGMENT_METADATA_ATTRIBUTE_NAME);
        
        if (fragmentBehaviour == null) {
            // In practice, we don't need to differentiate between a non-existing attribute and
            // a null-valued one. So we just execute the renderer.

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][TILES] No Fragment Behaviour object has been found in request. " +
                        "Normal execution will be triggered for attribute with value \"{}\"", 
                        new Object[] {value});
            }
        
            this.renderer.render(attribute, tilesRequestContext);
            
        } else {
            // There is a FragmentBehaviour object, so we need to know whether we are executing a
            // Thymeleaf page --in which case we will honor its value-- or a JSP/String attribute
            // --in which case we will ignore it and remove it.
            
            if (!(this.renderer instanceof ThymeleafAttributeRenderer)) {
                // It is not a Thymeleaf renderer, so we will just remove the attribute and ignore it.
                // Removing the attribute before executing the renderer allows us to avoid
                // scenarios such as TH1 -> JSP1 -> TH2, where the "displayOnlyChildren" flag
                // passed from TH1 to JSP1 (and ignored, as JSPs don't understand it) would 
                // end up affecting the way TH2 is rendered.

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][TILES] A Fragment Behaviour object has been found in request, " +
                            "and renderer is of class {}, which is not a Thymeleaf renderer. " +
                            "Fragment Behaviour object will be removed from request before rendering " +
                            "attribute with value \"{}\"", 
                            new Object[] {this.renderer.getClass().getName(), value});
                }
                
                request.removeAttribute(ThymeleafTilesNaming.FRAGMENT_METADATA_ATTRIBUTE_NAME);
                
            } else {

                if (logger.isTraceEnabled()) {
                    logger.trace("[THYMELEAF][TILES] A Fragment Behaviour object has been found in request, " +
                            "and renderer is of class {}, which is a Thymeleaf renderer. " +
                            "Fragment Behaviour will be used when rendering attribute with value \"{}\"", 
                            new Object[] {this.renderer.getClass().getName(), value});
                }
                
            }
            
            // In any case, we will render the attribute
            this.renderer.render(attribute, tilesRequestContext);
            
        }
        
        
        /*
         * At the end, remove the attribute anyway
         */
        request.removeAttribute(ThymeleafTilesNaming.FRAGMENT_METADATA_ATTRIBUTE_NAME);

        
        
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][TILES] Finished execution of wrapped renderer of class {} for attribute with " +
                    "value \"{}\"", new Object[] {this.renderer.getClass().getName(), value});
        }
        
    }
    

    
    
    
    
    
    
    

    
    
    /*
     * -------------------------------------------------
     *   WRAPPED METHODS
     * -------------------------------------------------
     */


    
    @Override
    public void write(final Object value, final Attribute attribute,
            final TilesRequestContext tilesRequestContext) throws IOException {
        this.renderer.write(value, attribute, tilesRequestContext);
    }


    
    
    public boolean isRenderable(final Object value, final Attribute attribute,
            final TilesRequestContext request) {
        return this.renderer.isRenderable(value, attribute, request);
    }




    @Override
    public boolean isRenderable(final Attribute attribute, final TilesRequestContext request) {
        return this.renderer.isRenderable(attribute, request);
    }




    @Override
    public void setRequestContextFactory(final TilesRequestContextFactory contextFactory) {
        this.renderer.setRequestContextFactory(contextFactory);
    }




    @Override
    public void setApplicationContext(final TilesApplicationContext applicationContext) {
        this.renderer.setApplicationContext(applicationContext);
    }




    @Override
    public void setAttributeEvaluatorFactory(final AttributeEvaluatorFactory attributeEvaluatorFactory) {
        this.renderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
    }




    @Override
    protected TilesRequestContext getRequestContext(final Object... requestItems) {
        throw new UnsupportedOperationException("Method getRequestContext() is not implemented by " + 
                this.getClass().getName() + " (it is protected)");
    }




    @Override
    protected boolean isPermitted(final TilesRequestContext request, final Set<String> roles) {
        throw new UnsupportedOperationException("Method isPermitted() is not implemented by " + 
                this.getClass().getName() + " (it is protected)");
    }


    
}
