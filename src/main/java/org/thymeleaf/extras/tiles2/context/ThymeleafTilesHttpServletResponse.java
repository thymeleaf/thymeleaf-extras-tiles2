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
package org.thymeleaf.extras.tiles2.context;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.thymeleaf.util.Validate;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 *
 */
public class ThymeleafTilesHttpServletResponse extends HttpServletResponseWrapper {

    private final PrintWriter printWriter;
    
    
    
    public ThymeleafTilesHttpServletResponse(final HttpServletResponse response, final Writer writer) {
        super(response);
        Validate.notNull(writer, "Writer cannot be null");
        this.printWriter = (writer instanceof PrintWriter? (PrintWriter) writer : new PrintWriter(writer)); 
    }
    


    @Override
    public PrintWriter getWriter() throws IOException {
        return this.printWriter;
    }

    
}
