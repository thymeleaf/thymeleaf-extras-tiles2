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
package org.thymeleaf.tiles2.factory;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.renderer.impl.BasicRendererFactory;
import org.thymeleaf.tiles2.localeresolver.LocaleResolverHolder;
import org.thymeleaf.tiles2.renderer.AbstractThymeleafAttributeRenderer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public abstract class AbstractThymeleafTilesContainerFactory 
        extends BasicTilesContainerFactory {

    public static final String THYMELEAF_ATTRIBUTE_TYPE = "thymeleaf";
    
    private final LocaleResolverHolder localeResolverHolderObj;
    
    
    public AbstractThymeleafTilesContainerFactory() {
        super();
        this.localeResolverHolderObj = new LocaleResolverHolder();
    }

    
    
    
    @Override
    protected final LocaleResolver createLocaleResolver(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        
        /*
         * We intercept the initalization of the locale resolver so that we can use
         * it at the thymeleaf attribute renderers.
         */
        
        final LocaleResolver localeResolver =
                createInterceptedLocaleResolver(applicationContext, contextFactory); 
        this.localeResolverHolderObj.setLocaleResolver(localeResolver);
        
        return localeResolver;
        
    }

    
    
    protected LocaleResolver createInterceptedLocaleResolver(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        return super.createLocaleResolver(applicationContext, contextFactory);
    }

    

    @Override
    protected void registerAttributeRenderers(
            final BasicRendererFactory rendererFactory,
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory,
            final TilesContainer container,
            final AttributeEvaluatorFactory attributeEvaluatorFactory) {

        super.registerAttributeRenderers(rendererFactory, applicationContext,
                contextFactory, container, attributeEvaluatorFactory);
        
        final AbstractThymeleafAttributeRenderer renderer = createAttributeRenderer(this.localeResolverHolderObj);
        renderer.setApplicationContext(applicationContext);
        renderer.setRequestContextFactory(contextFactory);
        renderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        rendererFactory.registerRenderer(THYMELEAF_ATTRIBUTE_TYPE, renderer);
        
    }

    

    
    protected abstract AbstractThymeleafAttributeRenderer createAttributeRenderer(final LocaleResolverHolder localeResolverHolder);
    
    
}
