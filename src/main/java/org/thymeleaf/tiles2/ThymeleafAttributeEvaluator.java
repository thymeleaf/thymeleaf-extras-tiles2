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
package org.thymeleaf.tiles2;

import java.util.Map;

import org.apache.tiles.context.TilesRequestContext;
import org.apache.tiles.evaluator.AbstractAttributeEvaluator;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class ThymeleafAttributeEvaluator extends AbstractAttributeEvaluator {

    
    public Object evaluate(final String expression, final TilesRequestContext request) {
        return "EXPRESSION: " + expression;
    }

    public void init(final Map<String, String> initParameters) {
        // TODO Auto-generated method stub
        
    }


    
    
    
}
