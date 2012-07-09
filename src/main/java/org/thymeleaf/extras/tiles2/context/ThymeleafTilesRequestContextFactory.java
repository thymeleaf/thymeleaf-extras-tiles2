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
import javax.servlet.jsp.PageContext;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesRequestContextFactory implements TilesRequestContextFactory {

    
    
    
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
                requestItems[1] instanceof IProcessingContext &&
                requestItems[2] instanceof HttpServletRequest &&
                requestItems[3] instanceof HttpServletResponse &&
                requestItems[4] instanceof Writer) {

            
            final TemplateEngine templateEngine = (TemplateEngine) requestItems[0];
            final IProcessingContext processingContext = (IProcessingContext) requestItems[1];
            final HttpServletRequest httpServletRequest = (HttpServletRequest) requestItems[2];
            final HttpServletResponse httpServletResponse = (HttpServletResponse) requestItems[3];
            final Writer writer = (Writer) requestItems[4];

            
            final ThymeleafTilesHttpServletRequest request =
                    computeRequest(httpServletRequest, templateEngine, processingContext);
            
            final ThymeleafTilesHttpServletResponse response =
                    computeResponse(httpServletResponse, writer);
            
            return new ThymeleafTilesRequestContext(tilesApplicationContext, request, response);

            
        } else if (requestItems.length == 1 && 
                       requestItems[0] instanceof ThymeleafTilesRequestContext) {
            
            
            return (ThymeleafTilesRequestContext) requestItems[0];
            
            
        } else if (requestItems.length == 1 && 
                       requestItems[0] instanceof PageContext) {
            // Substitution of JspTilesRequestContextFactory
            
            final PageContext pageContext = (PageContext) requestItems[0];
            
            final ThymeleafTilesHttpServletRequest request =
                    checkRequest((HttpServletRequest) pageContext.getRequest());
            final ThymeleafTilesHttpServletResponse response =
                    checkResponse((HttpServletResponse) pageContext.getResponse());
            
            return new ThymeleafTilesRequestContext(tilesApplicationContext, request, response);
            
            
        } else if (requestItems.length == 2 && 
                       requestItems[0] instanceof HttpServletRequest &&
                       requestItems[1] instanceof HttpServletResponse) {
            // Substitution of ServletTilesRequestContextFactory
            
            final ThymeleafTilesHttpServletRequest request =
                    checkRequest((HttpServletRequest) requestItems[0]);
            final ThymeleafTilesHttpServletResponse response =
                    checkResponse((HttpServletResponse) requestItems[1]);
            
            return new ThymeleafTilesRequestContext(tilesApplicationContext, request, response);
            
        }
        
        return null;
        
    }


    
    
    
    private static ThymeleafTilesHttpServletRequest computeRequest(
            final HttpServletRequest request, final TemplateEngine templateEngine, 
            final IProcessingContext processingContext) {
        
        if (request instanceof ThymeleafTilesHttpServletRequest) {
            return (ThymeleafTilesHttpServletRequest) request;
        }
            
        return new ThymeleafTilesHttpServletRequest(request, templateEngine, processingContext);
        
    }
    
    
    
    private static ThymeleafTilesHttpServletResponse computeResponse(
            final HttpServletResponse response, final Writer writer) {
        
        if (response instanceof ThymeleafTilesHttpServletResponse) {
            return (ThymeleafTilesHttpServletResponse) response;
        }
        
        return new ThymeleafTilesHttpServletResponse(response, writer);
        
    }
    
    
    
    
    private static ThymeleafTilesHttpServletRequest checkRequest(final HttpServletRequest request) {
        if (request instanceof ThymeleafTilesHttpServletRequest) {
            return (ThymeleafTilesHttpServletRequest) request;
        }
        throw new IllegalArgumentException("Request is not a " + ThymeleafTilesHttpServletRequest.class.getName());
    }
    
    

    private static ThymeleafTilesHttpServletResponse checkResponse(final HttpServletResponse response) {
        if (response instanceof ThymeleafTilesHttpServletResponse) {
            return (ThymeleafTilesHttpServletResponse) response;
        }
        throw new IllegalArgumentException("Response is not a " + ThymeleafTilesHttpServletResponse.class.getName());
    }
    
    

    
    
    @Deprecated
    public void init(final Map<String, String> configurationParameters) {
        // Nothing to do here.
    }



}
