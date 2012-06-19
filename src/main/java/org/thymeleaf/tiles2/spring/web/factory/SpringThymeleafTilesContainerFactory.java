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
package org.thymeleaf.tiles2.spring.web.factory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesApplicationContextAware;
import org.apache.tiles.context.TilesRequestContextFactory;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.Refreshable;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.PreparerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.tiles2.SpringLocaleResolver;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.thymeleaf.tiles2.factory.AbstractThymeleafTilesContainerFactory;
import org.thymeleaf.tiles2.localeresolver.LocaleResolverHolder;
import org.thymeleaf.tiles2.renderer.AbstractThymeleafAttributeRenderer;
import org.thymeleaf.tiles2.spring.web.configurer.SpringThymeleafTilesConfigurer;
import org.thymeleaf.tiles2.spring.web.renderer.SpringThymeleafAttributeRenderer;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class SpringThymeleafTilesContainerFactory 
        extends AbstractThymeleafTilesContainerFactory {
    
    
    private final SpringThymeleafTilesConfigurer configurer;
    
    
    
    public SpringThymeleafTilesContainerFactory(final SpringThymeleafTilesConfigurer configurer) {
        super();
        this.configurer = configurer;
    }
    
    
    

    @Override
    protected BasicTilesContainer instantiateContainer(final TilesApplicationContext context) {
        final boolean useMutableTilesContainer =
                TilesConfigurerSuperClassIntegration.getUseMutableTilesContainer(this.configurer);
        return (useMutableTilesContainer ? new CachingTilesContainer() : new BasicTilesContainer());
    }

    
    
    @Override
    protected void registerRequestContextFactory(final String className,
            final List<TilesRequestContextFactory> factories, final TilesRequestContextFactory parent) {
        // Avoid Tiles 2.2 warn logging when default RequestContextFactory impl class not found
        if (ClassUtils.isPresent(className, TilesConfigurer.class.getClassLoader())) {
            super.registerRequestContextFactory(className, factories, parent);
        }
    }

    
    
    @Override
    protected List<URL> getSourceURLs(final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        
        final String[] definitions =
                TilesConfigurerSuperClassIntegration.getDefinitions(this.configurer);
        
        if (definitions != null) {
            try {
                final List<URL> result = new LinkedList<URL>();
                for (String definition : definitions) {
                    result.addAll(applicationContext.getResources(definition));
                }
                return result;
            } catch (IOException ex) {
                throw new DefinitionsFactoryException("Cannot load definition URLs", ex);
            }
        }
        return super.getSourceURLs(applicationContext, contextFactory);
        
    }
    
    

    @Override
    protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory, final LocaleResolver resolver) {
        
        final BaseLocaleUrlDefinitionDAO dao = 
                super.instantiateLocaleDefinitionDao(applicationContext, contextFactory, resolver);
        
        final boolean checkRefresh =
                TilesConfigurerSuperClassIntegration.getCheckRefresh(this.configurer);
        
        if (checkRefresh && dao instanceof CachingLocaleUrlDefinitionDAO) {
            ((CachingLocaleUrlDefinitionDAO) dao).setCheckRefresh(checkRefresh);
        }
        
        return dao;
        
    }
    

    
    @Override
    protected DefinitionsReader createDefinitionsReader(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        
        final DigesterDefinitionsReader reader = new DigesterDefinitionsReader();
        
        final boolean validateDefinitions =
                TilesConfigurerSuperClassIntegration.getValidateDefinitions(this.configurer);
        
        if (!validateDefinitions){
            final Map<String,String> map = new HashMap<String,String>();
            map.put(DigesterDefinitionsReader.PARSER_VALIDATE_PARAMETER_NAME, Boolean.FALSE.toString());
            reader.init(map);
        }
        
        return reader;
        
    }

    
    
    @Override
    protected DefinitionsFactory createDefinitionsFactory(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory, final LocaleResolver resolver) {
        
        final Class<? extends DefinitionsFactory> definitionsFactoryClass =
                TilesConfigurerSuperClassIntegration.getDefinitionsFactoryClass(this.configurer);
        
        if (definitionsFactoryClass != null) {
            
            DefinitionsFactory factory = BeanUtils.instantiate(definitionsFactoryClass);
            if (factory instanceof TilesApplicationContextAware) {
                ((TilesApplicationContextAware) factory).setApplicationContext(applicationContext);
            }
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(factory);
        
            if (bw.isWritableProperty("localeResolver")) {
                bw.setPropertyValue("localeResolver", resolver);
            }
            if (bw.isWritableProperty("definitionDAO")) {
                bw.setPropertyValue("definitionDAO",
                        createLocaleDefinitionDao(applicationContext, contextFactory, resolver));
            }
            if (factory instanceof Refreshable) {
                ((Refreshable) factory).refresh();
            }
            return factory;
            
        }
        
        return super.createDefinitionsFactory(applicationContext, contextFactory, resolver);
        
    }

    
    
    @Override
    protected PreparerFactory createPreparerFactory(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        
        final Class<? extends PreparerFactory> preparerFactoryClass =
                TilesConfigurerSuperClassIntegration.getPreparerFactoryClass(this.configurer);
        
        if (preparerFactoryClass != null) {
            return BeanUtils.instantiate(preparerFactoryClass);
        }

        return super.createPreparerFactory(applicationContext, contextFactory);
        
    }

    
    
    @Override
    protected LocaleResolver createInterceptedLocaleResolver(
            final TilesApplicationContext applicationContext,
            final TilesRequestContextFactory contextFactory) {
        return new SpringLocaleResolver();
    }



    


    @Override
    protected AbstractThymeleafAttributeRenderer createAttributeRenderer(
            final LocaleResolverHolder localeResolverHolder) {
        
        return new SpringThymeleafAttributeRenderer(
                localeResolverHolder, this.configurer.getApplicationContext(), this.configurer.getTemplateEngine());
        
    }
    
    
    
}
