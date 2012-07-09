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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.TemplateAttributeRenderer;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesHttpServletRequestWrapper;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesProcessingContext;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContext;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class LocalVariableTemplateAttributeRenderer 
        extends TemplateAttributeRenderer {

    
    
    public LocalVariableTemplateAttributeRenderer() {
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
        
        /*
         * TODO Add utility methods to ThymeleafTilesRequestContext to:
         * 1. Return request, response
         * 2. Refresh itself
         */
        
        /*
         * TODO Modify refresh so that it also refreshes if a variable has changed value!
         */

        
        final ThymeleafTilesRequestContext requestContext = 
                ThymeleafAttributeRendererUtils.normalizeRequestContext(tilesRequestContext);

        
        ThymeleafTilesProcessingContext processingContext = requestContext.getProcessingContext();
        
        final HttpServletRequest request = processingContext.getContext().getHttpServletRequest();
        final HttpServletResponse response = processingContext.getContext().getHttpServletResponse();
        final ServletContext  servletContext = processingContext.getContext().getServletContext();
        
        final ThymeleafTilesHttpServletRequestWrapper requestWrapper = 
                new ThymeleafTilesHttpServletRequestWrapper(request);
                
        processingContext = processingContext.refresh(requestWrapper, response, servletContext);
        
        final ThymeleafTilesRequestContext newRequestContext = 
                new ThymeleafTilesRequestContext(
                        requestContext.getWrappedRequest(), requestContext.getTemplateEngine(), 
                        processingContext, requestContext.getWriter());
        
        
//        final ThymeleafTilesHttpServletRequestWrapper requestWrapper = 
//                new ThymeleafTilesHttpServletRequestWrapper(request);
        
        super.write(value, attribute, newRequestContext);
        
    }


    
    
}
