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
package org.thymeleaf.extras.tiles2.dialect;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesFragmentAttrProcessor;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesIncludeAttrProcessor;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesReplaceAttrProcessor;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesStringAttrProcessor;
import org.thymeleaf.extras.tiles2.dialect.processor.TilesSubstituteByAttrProcessor;
import org.thymeleaf.processor.IProcessor;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class TilesDialect extends AbstractDialect {

    public static final String DEFAULT_PREFIX = "tiles";
    
    
    public TilesDialect() {
        super();
    }

    
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }



    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
        processors.add(new TilesFragmentAttrProcessor());
        processors.add(new TilesStringAttrProcessor());
        processors.add(new TilesIncludeAttrProcessor());
        processors.add(new TilesSubstituteByAttrProcessor());
        processors.add(new TilesReplaceAttrProcessor());
        return processors;
    }

    
    
    
    
}
