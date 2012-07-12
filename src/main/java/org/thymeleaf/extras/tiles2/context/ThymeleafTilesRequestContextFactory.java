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
package org.thymeleaf.extras.tiles2.context;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesRequestContextFactoryAware;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.servlet.context.ExternalWriterHttpServletResponse;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ProcessingContext;
import org.thymeleaf.extras.tiles2.naming.ThymeleafTilesNaming;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesRequestContextFactory 
        implements TilesRequestContextFactory, TilesRequestContextFactoryAware {

    
    private static final Logger logger = LoggerFactory.getLogger(ThymeleafTilesRequestContextFactory.class);
    
    
    private TilesRequestContextFactory parent = null;
    
    
    
    public ThymeleafTilesRequestContextFactory() {
        super();
    }
    
    
    

    public TilesRequestContext createRequestContext(
            final TilesApplicationContext tilesApplicationContext, final Object... requestItems) {
        
        // Will require 5 request objects:
        //   1. TemplateEngine
        //   2. IProcessingContext
        //   3. HttpServletRequest
        //   4. HttpServletResponse
        //   5. Writer
        
        if (requestItems.length == 5 &&
                requestItems[0] instanceof TemplateEngine &&
                (requestItems[1] instanceof IProcessingContext || requestItems[1] instanceof IContext) &&
                requestItems[2] instanceof HttpServletRequest &&
                requestItems[3] instanceof HttpServletResponse &&
                requestItems[4] instanceof Writer) {

            
            
            final TemplateEngine templateEngine = (TemplateEngine) requestItems[0];
            final Object contextObject = requestItems[1];
            final HttpServletRequest request = (HttpServletRequest) requestItems[2];
            final HttpServletResponse response = (HttpServletResponse) requestItems[3];
            final Writer writer = (Writer) requestItems[4];
            
            final IProcessingContext processingContext =
                    (contextObject instanceof IProcessingContext?
                            (IProcessingContext)contextObject : new ProcessingContext((IContext)contextObject));
            
            /*
             * Add TemplateEngine, ProcessingContext and Writer as attributes to the request,
             * in case we have an scenario where a JSP calls another JSP which then calls a Thymeleaf
             * template (JSPs use PageContext, which references the original request and response objects).
             */
            
            request.setAttribute(ThymeleafTilesNaming.TEMPLATE_ENGINE_ATTRIBUTE_NAME, templateEngine);
            request.setAttribute(ThymeleafTilesNaming.PROCESSING_CONTEXT_ATTRIBUTE_NAME, processingContext);

            
            /*
             * We add our own writer to the response
             */
            final HttpServletResponse responseWithWriter = 
                    new ExternalWriterHttpServletResponse(
                            response, 
                            (writer instanceof PrintWriter? (PrintWriter)writer : new PrintWriter(writer)));

            /*
             * Delegate de creation of the request context, if possible (normally, a ServletTilesRequestContext will be created anyway)
             */
            final TilesRequestContext result =
                    (this.parent != null?
                            this.parent.createRequestContext(tilesApplicationContext, request, responseWithWriter) :
                            new ServletTilesRequestContext(tilesApplicationContext, request, responseWithWriter));

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Processed Thymeleaf Tiles Request Context. " +
                        "Created instance of {}", new Object[] {TemplateEngine.threadIndex(), (result != null? result.getClass().getName() : null)});
            }
            
            return result;
            
        }
        
        return null;
        
    }


    

    
    
    @Deprecated
    public void init(final Map<String, String> configurationParameters) {
        // Nothing to do here.
    }


    
    


    public void setRequestContextFactory(final TilesRequestContextFactory contextFactory) {
        this.parent = contextFactory;
    }



}
