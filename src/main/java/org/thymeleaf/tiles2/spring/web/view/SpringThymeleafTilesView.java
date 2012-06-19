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
package org.thymeleaf.tiles2.spring.web.view;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.tiles2.TilesView;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
public class SpringThymeleafTilesView extends TilesView {
    
    
    
    
    public SpringThymeleafTilesView() {
        super();
    }

    
    
    
    @Override
    protected void renderMergedOutputModel(final Map<String, Object> model,
            final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        if (model.containsKey(SpringContextVariableNames.SPRING_REQUEST_CONTEXT)) {
            throw new ServletException(
                    "Cannot expose request context in model attribute '" + SpringContextVariableNames.SPRING_REQUEST_CONTEXT +
                    "' because of an existing model object of the same name");
        }
        model.put(SpringContextVariableNames.SPRING_REQUEST_CONTEXT, new RequestContext(request, response, getServletContext(), model));
        
        super.renderMergedOutputModel(model, request, response);
        
    }
    
    
    


    
    
}
