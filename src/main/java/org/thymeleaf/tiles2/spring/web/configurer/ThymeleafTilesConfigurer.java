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
package org.thymeleaf.tiles2.spring.web.configurer;

import org.apache.tiles.startup.TilesInitializer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.tiles2.spring.web.startup.ThymeleafTilesInitializer;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafTilesConfigurer 
        extends TilesConfigurer
        implements ApplicationContextAware {

    
    protected static final boolean tiles22Present = ClassUtils.isPresent(
            "org.apache.tiles.evaluator.AttributeEvaluatorFactory", TilesConfigurer.class.getClassLoader());
    
    
    private TemplateEngine templateEngine = null; 
    private ApplicationContext applicationContext = null; 
    


    public ThymeleafTilesConfigurer() {
        super();
        if (!tiles22Present) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " requires Tiles version 2.2+");
        }
    }

    

    public TemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }


    @Required
    public void setTemplateEngine(final TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    
    
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    

    @Required
    public void setApplicationContext(final ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }





    /**
     * <p>
     *   Use a Thymeleaf-enabled initializer.
     * </p>
     */
    @Override
    protected TilesInitializer createTilesInitializer() {
        return new ThymeleafTilesInitializer(this);
    }

    
   
}
