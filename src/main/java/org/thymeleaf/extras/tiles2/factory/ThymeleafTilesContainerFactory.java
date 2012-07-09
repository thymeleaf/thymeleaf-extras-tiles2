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
package org.thymeleaf.extras.tiles2.factory;

import java.util.List;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.context.ChainedTilesRequestContextFactory;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.renderer.AttributeRenderer;
import org.apache.tiles.renderer.TypeDetectingAttributeRenderer;
import org.apache.tiles.renderer.impl.BasicRendererFactory;
import org.apache.tiles.renderer.impl.ChainedDelegateAttributeRenderer;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContextFactory;
import org.thymeleaf.extras.tiles2.renderer.LocalVariableTemplateAttributeRenderer;
import org.thymeleaf.extras.tiles2.renderer.ThymeleafAttributeRenderer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesContainerFactory 
        extends BasicTilesContainerFactory {

    
    public static final String THYMELEAF_RENDERER_NAME = "thymeleaf";
    public static final String JSP_RENDERER_NAME = "jsp";
    
    
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

        
        rendererFactory.registerRenderer(STRING_RENDERER_NAME,
                createStringAttributeRenderer(rendererFactory,
                        applicationContext, contextFactory, container,
                        attributeEvaluatorFactory));
        
        rendererFactory.registerRenderer(JSP_RENDERER_NAME,
                createJspAttributeRenderer(rendererFactory,
                        applicationContext, contextFactory, container,
                        attributeEvaluatorFactory));
        
        rendererFactory.registerRenderer(DEFINITION_RENDERER_NAME,
                createDefinitionAttributeRenderer(rendererFactory,
                        applicationContext, contextFactory, container,
                        attributeEvaluatorFactory));

        final AttributeRenderer thymeleafAttributeRenderer = 
                createThymeleafAttributeRenderer(rendererFactory,
                        applicationContext, contextFactory, container,
                        attributeEvaluatorFactory);
                        
        rendererFactory.registerRenderer(THYMELEAF_RENDERER_NAME, thymeleafAttributeRenderer);
        rendererFactory.registerRenderer(TEMPLATE_RENDERER_NAME, thymeleafAttributeRenderer);
        
    }



    @SuppressWarnings("unused")
    protected AttributeRenderer createJspAttributeRenderer(
            final BasicRendererFactory rendererFactory,
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory,
            final TilesContainer container,
            final AttributeEvaluatorFactory attributeEvaluatorFactory) {
        
        final LocalVariableTemplateAttributeRenderer templateRenderer = new LocalVariableTemplateAttributeRenderer();
        templateRenderer.setApplicationContext(applicationContext);
        templateRenderer.setRequestContextFactory(contextFactory);
        templateRenderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        
        return templateRenderer;
        
    }



    @SuppressWarnings("unused")
    protected AttributeRenderer createThymeleafAttributeRenderer(
            final BasicRendererFactory rendererFactory,
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory,
            final TilesContainer container,
            final AttributeEvaluatorFactory attributeEvaluatorFactory) {
        
        final ThymeleafAttributeRenderer renderer = new ThymeleafAttributeRenderer();
        renderer.setApplicationContext(applicationContext);
        renderer.setRequestContextFactory(contextFactory);
        renderer.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        
        return renderer;
        
    }



    
    
    @Override
    protected AttributeRenderer createDefaultAttributeRenderer(
            final BasicRendererFactory rendererFactory,
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory,
            final TilesContainer container,
            final AttributeEvaluatorFactory attributeEvaluatorFactory) {
        
        /*
         * By default (when no type is specified) the template values will evaluated as:
         *   1. Definitions.
         *   2. Thymeleaf templates.
         */
        
        final ChainedDelegateAttributeRenderer chain = new ChainedDelegateAttributeRenderer();
        chain.addAttributeRenderer(
                (TypeDetectingAttributeRenderer) rendererFactory.getRenderer(DEFINITION_RENDERER_NAME));
        chain.addAttributeRenderer(
                (TypeDetectingAttributeRenderer) rendererFactory.getRenderer(THYMELEAF_RENDERER_NAME));
        chain.setApplicationContext(applicationContext);
        chain.setRequestContextFactory(contextFactory);
        chain.setAttributeEvaluatorFactory(attributeEvaluatorFactory);
        return chain;
        
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
