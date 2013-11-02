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
package org.thymeleaf.extras.tiles2.renderer;

import java.util.List;

import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   This wrapper around {@link IFragmentSpec} objects removes the root nodes of the selection, allowing the
 *   implementation of only-children nodes inclusion operations.
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 */
final class FragmentSpecRootRemovingWrapper implements IFragmentSpec {


    private final IFragmentSpec wrapped;



    public FragmentSpecRootRemovingWrapper(final IFragmentSpec wrapped) {
        super();
        Validate.notNull(wrapped, "Wrapped fragment spec cannot be null");
        this.wrapped = wrapped;
    }


    public List<Node> extractFragment(final Configuration configuration, final List<Node> nodes) {

        final List<Node> extractedNodes = this.wrapped.extractFragment(configuration, nodes);

        final Element containerElement = new Element("container");

        for (final Node extractedNode : extractedNodes) {
            // This is done in this indirect way in order to preserver internal structures like e.g. local variables.
            containerElement.addChild(extractedNode);
            containerElement.extractChild(extractedNode);
        }

        final List<Node> extractedChildren = containerElement.getChildren();
        containerElement.clearChildren();

        return extractedChildren;

    }

}
