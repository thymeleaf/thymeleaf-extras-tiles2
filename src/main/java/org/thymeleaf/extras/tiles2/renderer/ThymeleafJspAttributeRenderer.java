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
package org.thymeleaf.extras.tiles2.renderer;

import java.io.IOException;

import org.apache.tiles.Attribute;
import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.impl.InvalidTemplateException;
import org.apache.tiles.renderer.impl.TemplateAttributeRenderer;
import org.thymeleaf.extras.tiles2.context.ThymeleafTilesRequestContext;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafJspAttributeRenderer 
        extends TemplateAttributeRenderer {

    
    
    public ThymeleafJspAttributeRenderer() {
        super();
    }

    
    
    @Override
    public void write(final Object value, final Attribute attribute,
            final TilesRequestContext tilesRequestContext) throws IOException {

        if (value == null) {
            throw new InvalidTemplateException("Cannot render a null template");
        }
        if (!(value instanceof String)) {
            throw new InvalidTemplateException(
                    "Cannot render a template that is not a String ('" + value.getClass().getName() +"')");
        }

        final ThymeleafTilesRequestContext requestContext = 
                (ThymeleafTilesRequestContext) tilesRequestContext;
        
        super.write(value, attribute, requestContext);
        
    }


    
    
}
