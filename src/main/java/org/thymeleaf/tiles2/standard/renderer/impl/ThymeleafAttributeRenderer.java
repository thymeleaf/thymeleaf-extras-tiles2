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
package org.thymeleaf.tiles2.standard.renderer.impl;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.renderer.impl.AbstractBaseAttributeRenderer;
import org.apache.tiles.servlet.context.ServletTilesRequestContext;
import org.apache.tiles.servlet.context.ServletUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.tiles2.standard.context.ThymeleafTilesServletUtils;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafAttributeRenderer 
        extends AbstractBaseAttributeRenderer {

    
    
    public ThymeleafAttributeRenderer() {
        super();
    }

    
    
    
    @Override
    public void write(final Object value, final Attribute attribute,
            final TilesRequestContext request) throws IOException {

        Validate.notNull(value, "Cannot render a null template");
        Validate.isTrue((value instanceof String), 
                "Cannot render a template that is not a String ('" + value.getClass().getName() +"')");

        final String templateName = (String) value;
        final Writer writer = request.getWriter();
        
        final ServletTilesRequestContext servletRequest = ServletUtil.getServletRequest(request);
        final HttpServletRequest httpServletRequest = servletRequest.getRequest();

        final ServletContext servletContext = httpServletRequest.getSession().getServletContext();
        if (servletContext == null) {
            throw new IllegalStateException("Cannot retrieve servlet context");
        }
        
        final TemplateEngine templateEngine = 
                ThymeleafTilesServletUtils.getTemplateEngine(servletContext);

        final WebContext context = new WebContext(httpServletRequest, servletContext);

        templateEngine.process(templateName, context, writer);
        
    }
    

    
    
}
