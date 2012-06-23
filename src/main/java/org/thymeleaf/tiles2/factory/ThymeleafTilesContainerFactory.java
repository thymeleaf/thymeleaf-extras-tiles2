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

import java.util.List;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.ChainedTilesRequestContextFactory;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.renderer.impl.BasicRendererFactory;
import org.thymeleaf.tiles2.context.ThymeleafTilesRequestContextFactory;
import org.thymeleaf.tiles2.renderer.ThymeleafAttributeRenderer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafTilesContainerFactory 
        extends BasicTilesContainerFactory {

    
    
    public ThymeleafTilesContainerFactory() {
        super();
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
        
        final ThymeleafAttributeRenderer renderer = new ThymeleafAttributeRenderer();
        renderer.setApplicationContext(applicationContext);
        renderer.setRequestContextFactory(contextFactory);
        renderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        rendererFactory.registerRenderer(ThymeleafAttributeRenderer.THYMELEAF_ATTRIBUTE_TYPE, renderer);
        
    }




    
    
    @Override
    protected List<TilesRequestContextFactory> getTilesRequestContextFactoriesToBeChained(
            final ChainedTilesRequestContextFactory parent) {

        final List<TilesRequestContextFactory> factories = super.getTilesRequestContextFactoriesToBeChained(parent);
        registerRequestContextFactory(
                ThymeleafTilesRequestContextFactory.class.getName(),
                factories, parent);
        
        return factories;
        
    }

    

    
    
}
