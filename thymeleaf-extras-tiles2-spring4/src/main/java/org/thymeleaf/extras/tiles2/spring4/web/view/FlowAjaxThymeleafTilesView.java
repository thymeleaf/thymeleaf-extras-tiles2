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
package org.thymeleaf.extras.tiles2.spring4.web.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;


/**
 * <p>
 *   Subclass of {@link ThymeleafTilesView} adding compatibility with AJAX events in
 *   Spring WebFlow. This allows this View implementation
 *   to be able to return only <i>fragments</i> of the page.
 * </p>
 * <p>
 *   These rendering of fragments is used, for example, in Spring WebFlow's &lt;render&gt;
 *   instructions (though not only).
 * </p>
 * <p>
 *   This view searches for a comma-separated list of <i>fragment names</i> in a request
 *   parameter called <kbd>fragments</kbd>.
 * </p>
 * <p>
 *   When using Tiles, fragments are considered to be <b>Tiles attribute names</b> (from a Tiles
 *   definition).
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 */
public class FlowAjaxThymeleafTilesView extends AjaxThymeleafTilesView {


    
    public FlowAjaxThymeleafTilesView() {
        super();
    }

    
    @Override
    @SuppressWarnings("rawtypes")
    protected String[] getRenderFragments(
            final Map model, final HttpServletRequest request, final HttpServletResponse response) {
        
        final RequestContext context = RequestContextHolder.getRequestContext();
        if (context == null) {
            return super.getRenderFragments(model, request, response);
        }
        
        final String[] fragments = (String[]) context.getFlashScope().get(View.RENDER_FRAGMENTS_ATTRIBUTE);
        if (fragments == null) {
            return super.getRenderFragments(model, request, response);
        }
        return fragments;
        
    }
    
    
    
}
