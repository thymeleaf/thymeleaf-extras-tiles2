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
import org.thymeleaf.Template;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.TemplateRepository;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dom.Node;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.util.Validate;


/**
 * <p>
 *   Object modelling the result of resolving a fragment specification as allowed by the features implemented
 *   by Tiles integration.
 * </p>
 * <p>
 *   Depending on whether the Standard Dialect has been enabled or not, the specification of fragments in Tiles
 *   definitions will allow the execution of stadard expressions (e.g. for determining the name of the template to
 *   be inserted) or not.
 * </p>
 * <p>
 *   Note that, as a difference with Standard fragment specifications, Tiles fragment specifications <b>do not
 *   allow the presence of fragment parameters.</b>
 * </p>
 *
 * @author Daniel Fern&aacute;ndez
 *
 * @since 2.1.0
 *
 */
public final class TilesFragment {

    private final String templateName;
    private final IFragmentSpec fragmentSpec;


    /**
     * <p>
     *   Create a new instance of this class.
     * </p>
     *
     * @param templateName the name of the template that will be resolved and parsed, null if
     *                     fragment is to be executed on the current template.
     * @param fragmentSpec the fragment spec that will be applied to the template, once parsed.
     */
    public TilesFragment(final String templateName, final IFragmentSpec fragmentSpec) {
        super();
        // templateName can be null if target template is the current one
        Validate.notNull(fragmentSpec, "Fragment spec cannot be null or empty");
        Validate.notNull(templateName, "Template name cannot be null or 'this' in Tiles fragment selections");
        this.templateName = templateName;
        this.fragmentSpec = fragmentSpec;
    }

    /**
     * <p>
     *   Create a new instance of this class from an already-created
     *   {@link org.thymeleaf.standard.fragment.StandardFragment} object
     * </p>
     *
     * @param standardFragment the standard fragment object to be used as a base.
     */
    public TilesFragment(final StandardFragment standardFragment) {

        super();
        // templateName can be null if target template is the current one
        Validate.notNull(standardFragment, "Standard Fragment cannot be null");

        this.templateName = standardFragment.getTemplateName();
        this.fragmentSpec = standardFragment.getFragmentSpec();

        Validate.notNull(this.templateName, "Template name cannot be null or 'this' in Tiles fragment selections");

        if (standardFragment.getParameters() != null && standardFragment.getParameters().size() > 0) {
            throw new TemplateProcessingException(
                    "Tiles-based fragment selections cannot specify parameters. But selection for " +
                    "template \"" + this.templateName + "\" and fragment spec " +
                    "\"" + this.fragmentSpec.toString() + "\" does specify some.");
        }

    }



    /**
     * <p>
     *   Returns the name of the template that will be resolved and parsed.
     * </p>
     *
     * @return the template name.
     */
    public String getTemplateName() {
        return this.templateName;
    }



    /**
     * <p>
     *   Returns the {@link org.thymeleaf.fragment.IFragmentSpec} that will be applied to the template.
     * </p>
     *
     * @return the fragment spec.
     */
    public IFragmentSpec getFragmentSpec() {
        return this.fragmentSpec;
    }



    /**
     * <p>
     *   Read the specified template from {@link org.thymeleaf.TemplateRepository}, and then apply
     *   the {@link org.thymeleaf.fragment.IFragmentSpec} to the result of parsing it (the template).
     * </p>
     *
     * @param configuration the configuration to be used for resolving the template and
     *        processing the fragment spec.
     * @param context the processing context to be used for resolving and parsing the template.
     * @param templateRepository the template repository to be asked for the template.
     * @return the result of parsing + applying the fragment spec.
     */
    public List<Node> extractFragment(
            final Configuration configuration, final IProcessingContext context,
            final TemplateRepository templateRepository) {

        String targetTemplateName = getTemplateName();

        final TemplateProcessingParameters fragmentTemplateProcessingParameters = 
                new TemplateProcessingParameters(configuration, targetTemplateName, context);
        
        final Template parsedFragmentTemplate = 
                templateRepository.getTemplate(fragmentTemplateProcessingParameters);
        
        final List<Node> nodes =
                this.fragmentSpec.extractFragment(configuration, parsedFragmentTemplate.getDocument().getChildren());

        /*
         * CHECK RETURNED NODES: if there is only one node, check whether it contains a fragment signature (normally,
         * a "th:fragment" attribute). If so, let the signature process the parameters before being applied. If no
         * signature is found, then just apply the parameters to every returning node.
         */

        if (nodes == null) {
            return null;
        }

        // Detach nodes from their parents, before returning them. This might help the GC.
        for (final Node node : nodes) {
            if (node.hasParent()) {
                node.getParent().clearChildren();
            }
        }

        return nodes;

    }


}

