/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
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

import org.thymeleaf.util.Validate;

public class FragmentMetadata {

    private final String attributeOrDefinitionName;
    private boolean displayOnlyChildren = false;
    
    
    public FragmentMetadata(final String attributeOrDefinitionName) {
        super();
        Validate.notNull(attributeOrDefinitionName, "Attribute/Definition name cannot be null");
        this.attributeOrDefinitionName = attributeOrDefinitionName;
    }


    public String getAttributeOrDefinitionName() {
        return this.attributeOrDefinitionName;
    }


    public boolean isDisplayOnlyChildren() {
        return this.displayOnlyChildren;
    }


    public void setDisplayOnlyChildren(final boolean displayOnlyChildren) {
        this.displayOnlyChildren = displayOnlyChildren;
    }
    
    
}
