/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2013, The THYMELEAF team (http://www.thymeleaf.org)
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
package org.thymeleaf.extras.tiles2.spring4.web.startup;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.startup.AbstractTilesInitializer;
import org.thymeleaf.extras.tiles2.spring4.web.configurer.ThymeleafTilesConfigurer;
import org.thymeleaf.extras.tiles2.spring4.web.factory.ThymeleafTilesContainerFactory;


/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesInitializer extends AbstractTilesInitializer {
        
    private final ThymeleafTilesConfigurer configurer;

    
    public ThymeleafTilesInitializer(final ThymeleafTilesConfigurer configurer) {
        super();
        this.configurer = configurer;
    }
    
    
    @Override
    protected AbstractTilesContainerFactory createContainerFactory(final TilesApplicationContext context) {
        return new ThymeleafTilesContainerFactory(this.configurer);
    }
   
   
}
