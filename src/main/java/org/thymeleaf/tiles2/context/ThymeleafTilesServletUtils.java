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
package org.thymeleaf.tiles2.context;

import javax.servlet.ServletContext;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.util.Validate;





/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public final class ThymeleafTilesServletUtils {

    
    public static final String TEMPLATE_ENGINE_SERVLET_CONTEXT_ATTRIBUTE_NAME =
            "org.thymeleaf.tiles2.TemplateEngine";
    
    

    public static void setTemplateEngine(final ServletContext servletContext, final TemplateEngine templateEngine) {
        Validate.notNull(servletContext, "Servlet Context cannot be null");
        servletContext.setAttribute(TEMPLATE_ENGINE_SERVLET_CONTEXT_ATTRIBUTE_NAME, templateEngine);
    }

    
    public static TemplateEngine getTemplateEngine(final ServletContext servletContext) {
        Validate.notNull(servletContext, "Servlet Context cannot be null");
        return (TemplateEngine) servletContext.getAttribute(TEMPLATE_ENGINE_SERVLET_CONTEXT_ATTRIBUTE_NAME);
    }
    
    
    private ThymeleafTilesServletUtils() {
        super();
    }
    
    
}
