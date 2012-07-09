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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.context.TilesRequestContextWrapper;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesRequestContext extends TilesRequestContextWrapper {


    private final ThymeleafTilesHttpServletRequest request;
    private final ThymeleafTilesHttpServletResponse response;
    private final Object[] requestObjects;


    
    
    

    public ThymeleafTilesRequestContext(final TilesApplicationContext tilesApplicationContext,
            final ThymeleafTilesHttpServletRequest request, final ThymeleafTilesHttpServletResponse response) {
        
        super(new ServletTilesRequestContext(tilesApplicationContext, request, response));
        
        Validate.notNull(request, "Request cannot be null");
        Validate.notNull(response, "Response cannot be null");
        
        this.request = request;
        this.response = response;
        this.requestObjects = new Object[] { this.request, this.response };

    }


    
    @Override
    public void dispatch(final String path) throws IOException {
        // These dispatch/include methods are called by TemplateAttributeRenderers
        // when including a JSP attribute.
        include(path);
    }


    
    @Override
    public void include(final String path) throws IOException {
        // These dispatch/include methods are called by TemplateAttributeRenderers
        // when including a JSP attribute.

        ServletUtil.setForceInclude(this.request, true);
        
        final RequestDispatcher requestDispatcher = this.request.getRequestDispatcher(path);
        if (requestDispatcher == null) {
            throw new IOException("Included path \"" + path + "\" has no associated Request Dispatcher");
        }

        try {
            
            requestDispatcher.include(this.request, this.response);
            
        } catch (final ServletException e) {
            // Wraps servlet exception as an IOException, as preferred by Tiles
            throw ServletUtil.wrapServletException(e, "Exception thrown while including path \"" + path + "\".");
        }
        
    }

    


    
    
    @Override
    public Writer getWriter() throws IOException {
        return this.response.getWriter();
    }

    
    @Override
    public PrintWriter getPrintWriter() throws IOException {
        return this.response.getWriter();
    }


    
    
    public TemplateEngine getTemplateEngine() {
        return this.request.getTemplateEngine();
    }
    
    
    public IProcessingContext getProcessingContext() {
        return this.request.getProcessingContext();
    }
    
    
    public ThymeleafTilesHttpServletRequest getRequest() {
        return this.request;
    }
    
    
    public ThymeleafTilesHttpServletResponse getResponse() {
        return this.response;
    }
    


    
    
    @Override
    public Object[] getRequestObjects() {
        return this.requestObjects;
    }



}
