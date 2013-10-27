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
package org.thymeleaf.extras.tiles2.dialect.processor;

import org.thymeleaf.processor.attr.AbstractNoOpAttrProcessor;

/**
 * Marks an area as a fragment to be included as a Tiles attribute.
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class TilesFragmentAttrProcessor 
            extends AbstractNoOpAttrProcessor {

    public static final int ATTR_PRECEDENCE = 1500;
    public static final String ATTR_NAME = "fragment";

    public TilesFragmentAttrProcessor() {
        super(ATTR_NAME);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

}
