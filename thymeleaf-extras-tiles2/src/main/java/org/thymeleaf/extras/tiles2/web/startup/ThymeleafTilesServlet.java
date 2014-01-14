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
package org.thymeleaf.extras.tiles2.web.startup;

import org.apache.tiles.startup.TilesInitializer;
import org.apache.tiles.web.startup.AbstractTilesInitializerServlet;
import org.thymeleaf.extras.tiles2.startup.ThymeleafTilesInitializer;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesServlet 
        extends AbstractTilesInitializerServlet {

    
    private static final long serialVersionUID = 199240475754703844L;
    
    

    public ThymeleafTilesServlet() {
        super();
    }
    
    
    

    @Override
    protected TilesInitializer createTilesInitializer() {
        return new ThymeleafTilesInitializer();
    }

    
}
