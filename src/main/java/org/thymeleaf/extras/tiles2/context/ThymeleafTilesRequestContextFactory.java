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

import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesRequestContextFactoryAware;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.extras.tiles2.naming.ThymeleafRequestAttributeNaming;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafTilesRequestContextFactory 
        implements TilesRequestContextFactory, TilesRequestContextFactoryAware {


    private TilesRequestContextFactory parentContextFactory;

    
    
    
    
    public ThymeleafTilesRequestContextFactory() {
        super();
    }
    
    
    

    public TilesRequestContext createRequestContext(
            final TilesApplicationContext tilesApplicationContext, final Object... requestItems) {
        
        // Will require 5 request objects:
        //   1. TemplateEngine
        //   2. IContext
        //   3. HttpServletRequest
        //   4. HttpServletResponse
        //   5. Writer
        
        if (requestItems.length == 5 &&
                requestItems[0] instanceof TemplateEngine &&
                requestItems[1] instanceof IContext &&
                requestItems[2] instanceof HttpServletRequest &&
                requestItems[3] instanceof HttpServletResponse &&
                requestItems[4] instanceof Writer) {

            final TemplateEngine templateEngine = (TemplateEngine) requestItems[0];
            final IContext context = (IContext) requestItems[1];
            final HttpServletRequest request = (HttpServletRequest) requestItems[2];
            final HttpServletResponse response = (HttpServletResponse) requestItems[3];
            final Writer writer = (Writer) requestItems[4];
            
            /*
             * Prepare request by adding attributes for template engine and context,
             * just in case a template of a different kind (e.g. a JSP) tries to execute
             * a thymeleaf attribute.
             */
            request.setAttribute(ThymeleafRequestAttributeNaming.TEMPLATE_ENGINE, templateEngine);
            request.setAttribute(ThymeleafRequestAttributeNaming.CONTEXT, context);
            
            final TilesRequestContext enclosedRequest =
                    (this.parentContextFactory != null?
                            this.parentContextFactory.createRequestContext(tilesApplicationContext, request, response) :
                            new ServletTilesRequestContext(tilesApplicationContext, request, response));

            return new ThymeleafTilesRequestContext(enclosedRequest, templateEngine, context, writer);
            
        } else if (requestItems.length == 1 && 
                       requestItems[0] instanceof ThymeleafTilesRequestContext) {
            
            return (ThymeleafTilesRequestContext) requestItems[0];
            
        }
        
        return null;
        
    }



    @Deprecated
    public void init(final Map<String, String> configurationParameters) {
        // Nothing to do here.
    }



    public void setRequestContextFactory(final TilesRequestContextFactory parentContextFactory) {
        this.parentContextFactory = parentContextFactory;
    }


}
