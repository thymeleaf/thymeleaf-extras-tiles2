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
package org.thymeleaf.tiles2.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.renderer.impl.AbstractBaseAttributeRenderer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.tiles2.localeresolver.LocaleResolverHolder;
import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractThymeleafAttributeRenderer 
        extends AbstractBaseAttributeRenderer {

    
    private final LocaleResolverHolder localeResolverHolder;
    
    
    
    public AbstractThymeleafAttributeRenderer(final LocaleResolverHolder localeResolverHolder) {
        super();
        Validate.notNull(localeResolverHolder, "LocaleResolver holder cannot be null");
        this.localeResolverHolder = localeResolverHolder;
    }

    
    
    
    @Override
    public void write(final Object value, final Attribute attribute,
            final TilesRequestContext tilesRequestContext) throws IOException {

        Validate.notNull(value, "Cannot render a null template");
        Validate.isTrue((value instanceof String), 
                "Cannot render a template that is not a String ('" + value.getClass().getName() +"')");

        final String templateName = (String) value;
        final Writer writer = tilesRequestContext.getWriter();

        // No variables to be added at this level
        final Map<String,Object> variables = new LinkedHashMap<String, Object>();
        
        final LocaleResolver localeResolver = this.localeResolverHolder.getLocaleResolver();
        final Locale locale = localeResolver.resolveLocale(tilesRequestContext);
        
        final TemplateEngine templateEngine = getTemplateEngine(templateName, tilesRequestContext);
        final IContext context = getContext(templateName, tilesRequestContext, locale, variables);
        
        templateEngine.process(templateName, context, writer);
        
    }
    
    
    
    protected abstract TemplateEngine getTemplateEngine(
            final String templateName, final TilesRequestContext tilesRequestContext);
    

    protected abstract IContext getContext(
            final String templateName, final TilesRequestContext tilesRequestContext, 
            final Locale locale, final Map<String,Object> variables);
    
    
}
