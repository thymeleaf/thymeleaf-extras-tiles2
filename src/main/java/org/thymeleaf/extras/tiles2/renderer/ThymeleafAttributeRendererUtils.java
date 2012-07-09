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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesProcessingContext;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContext;
import org.thymeleaf.extras.tiles2.naming.ThymeleafRequestAttributeNaming;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public final class ThymeleafAttributeRendererUtils {


    
    public static ThymeleafTilesRequestContext normalizeRequestContext(
            final TilesRequestContext tilesRequestContext) throws IOException {
        
        // Request could be Thymeleaf's own type or maybe a type from another
        // technology, but in that case we will need some attributes in the request
        // (the template engine and the context).
        
        if (!(tilesRequestContext instanceof ThymeleafTilesRequestContext)) {

            // If our request context is not a Thymeleaf one, could be because we are trying to render
            // a Thymeleaf attribute from a non-thymeleaf page, typically a JSP.
            final ServletTilesRequestContext servletTilesRequestContext =
                    ServletUtil.getServletRequest(tilesRequestContext);

            final HttpServletRequest httpServletRequest = servletTilesRequestContext.getRequest();
            final HttpServletResponse httpServletResponse = servletTilesRequestContext.getResponse();
            
            final TemplateEngine templateEngine = 
                    (TemplateEngine) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE);
            final IProcessingContext processingContext = 
                    (IProcessingContext) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.PROCESSING_CONTEXT);
            ThymeleafTilesProcessingContext thymeleafTilesProcessingContext = null;

            if (templateEngine == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " a TemplateEngine object must be present as a request attribute with name \"" + 
                        ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE + "\". Make sure the sequence of " +
                        "request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            if (processingContext == null) {
                final IWebContext context = 
                        (IWebContext) httpServletRequest.getAttribute(ThymeleafRequestAttributeNaming.CONTEXT);
                if (context == null) {
                    throw new InvalidTemplateException(
                            "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                            " either an IProcessingContext object must be present as a request attribute with name \"" + 
                            ThymeleafRequestAttributeNaming.PROCESSING_CONTEXT + "\" or an IContext object must " +
                            "be present as a request attribute with name \"" + ThymeleafRequestAttributeNaming.CONTEXT + 
                            "\". Make sure the sequence of request items you use when calling Tiles is: " +
                            "(TemplateEngine, [IProcessingContext|IContext], HttpServletRequest, HttpServletResponse, Writer)");
                }
                thymeleafTilesProcessingContext = new ThymeleafTilesProcessingContext(context);
            } else {
                if (processingContext instanceof ThymeleafTilesProcessingContext) {
                    thymeleafTilesProcessingContext = (ThymeleafTilesProcessingContext) processingContext;
                } else {
                    thymeleafTilesProcessingContext = new ThymeleafTilesProcessingContext(processingContext);
                }
            }
            if (httpServletResponse == null) {
                throw new InvalidTemplateException(
                        "Cannot render template: If Tiles Request Context is not of class " + ThymeleafTilesRequestContext.class.getName() +
                        " an HttpServletResponse object must be specified (and in this request context, it is null). " +
                        "Make sure the sequence of request items you use when calling Tiles is: " +
                        "(TemplateEngine, IContext, HttpServletRequest, HttpServletResponse, Writer)");
            }
            
            return new ThymeleafTilesRequestContext(
                        tilesRequestContext, templateEngine, thymeleafTilesProcessingContext, tilesRequestContext.getWriter());
            
        }

        
        // Request context is of Thymeleaf's own type
        return (ThymeleafTilesRequestContext) tilesRequestContext;
            
        
    }
    


    
    
    private ThymeleafAttributeRendererUtils() {
        super();
    }
    
}
