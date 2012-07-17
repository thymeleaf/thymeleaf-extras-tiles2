
Thymeleaf - Tiles 2 integration module
======================================

------------------------------------------------------------------------------

Requirements
------------

  *   Thymeleaf **2.0.10**
  *   Apache Tiles 2 version **2.2.1+** (**2.2.2** recommended)
  *   Web environment (Tiles integration cannot work offline)

Features
--------

  *   *(Optional)* **Spring MVC 3** integration
  *   Use Thymeleaf in your **Tiles definitions**:
    *   Use Thymeleaf templates.
    *   Include Thymeleaf templates (or fragments of templates) as attributes.
    *   Compatible with Tiles definition wildcards (Tiles 2.2.2+).
	*   Thymeleaf template/attribute definitions can include selectors 
        (similar to `th:include`/`th:substituteby`):
	  *   By fragment: `"template :: fragment"`
	  *   By DOM selector (XPath-like): `"template :: [//div[@id='content']]"`
	  *   Can include Standard Expressions: 
	      `"${templateName} :: ${conf.fragName}"`
  *   New **`tiles` dialect**:
    *   `tiles:include` / `tiles:substituteby` for including Tiles attributes.
	*   `tiles:fragment` for signaling fragments to be included as attributes.
	*   `tiles:string` for inserting String-type Tiles attributes.
  *   Enable **Natural templating** in your Tiles markup fragments:
    *   It allows you to use only specific parts of your templates as *Tiles
	    attributes*.
	*   You can add markup and styling surrounding those *specific parts* for
        static prototyping purposes.
  *   **Mix JSPs and Thymeleaf templates** in the same definition, for better
      legacy integration / migration.
    *   Thymeleaf fragment templates see variables defined in higher-level 
	    containing layout templates.
    *   JSP fragments see variables defined in higher-level containing 
	    JSP/Thymeleaf templates. And viceversa.

------------------------------------------------------------------------------

	
Configuration with Spring
-------------------------

In order to use Apache Tiles 2 with Thymeleaf in your Spring MVC application,
you will first need to configure your application in the usual way for
Thymeleaf applications (*TemplateEngine* bean, *template resolvers*, etc.),
and then create an instance of the `ThymeleafTilesConfigurer` (similar to
the Spring Tiles configurer for JSP), like:

    <bean id="tilesConfigurer" class="org.thymeleaf.extras.tiles2.spring.web.configurer.ThymeleafTilesConfigurer">
      <property name="definitions">
        <list>
          <value>/WEB-INF/tiles-defs.xml</value>
        </list>
      </property>
    </bean>

Also, you will need to configure your Thymeleaf *view resolver* in order to
use a specialized Thymeleaf-Tiles2 view class:

    <bean id="tilesViewResolver" class="org.thymeleaf.spring3.view.ThymeleafViewResolver">
      <property name="viewClass" value="org.thymeleaf.extras.tiles2.spring.web.view.ThymeleafTilesView"/>
      <property name="templateEngine" ref="templateEngine" />
    </bean>

...and finally, add the Tiles dialect to your Template Engine so that you
can use the `tiles:*` attributes:

    <bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine">
      ...
      <property name="additionalDialects">
        <set>
          <bean class="org.thymeleaf.extras.tiles2.dialect.TilesDialect"/>
        </set>
      </property>
	  ...
    </bean>

And that's all! Now you can make your controller methods return Tiles 
definition names as view names and everything should work fine.


Configuration and usage without Spring
--------------------------------------

Following the standard configuration mechanisms in Tiles 2.2, in order to use Thymeleaf + Tiles in
a non-Spring application, you should:

  *   Either configure an `org.thymeleaf.extras.tiles2.web.startup.ThymeleafTilesListener` at your `web.xml`
  *   ...or configure an `org.thymeleaf.extras.tiles2.web.startup.ThymeleafTilesServlet`, also at your `web.xml`.

Both of these artifacts declare an initialize a Thymeleaf-enabled `TilesContainer` instance, which you can
access with:

    final TilesContainer tiles = ServletUtil.getContainer(servletContext);
    
...and then execute specifying the definition to be executed and the following sequence of *request items*:

  1.   The Thymeleaf *template engine*.
  2.   The Thymeleaf *context* (`IContext`)
  3.   The `HttpServletRequest`
  4.   The `HttpServletResponse`
  5.   The `java.io.Writer` the result should be written to (for example, `response.getWriter()`)
  
    tiles.render("myDefinition", templateEngine, ctx, request, response, writer);
  
  
Using Thymeleaf in your definition files
----------------------------------------

Using Thymeleaf in your definition files (usually called something like
`tiles-defs.xml`) is easy. These are the key points:
  
  *   Thymeleaf is now the default *template type*, instead of JSP.
    *   You can use `type="thymeleaf"` or simply omit `type`, for both your
        templates and attributes.
	*   You can use `type="jsp"` for your JSP templates and attributes.
  *   Thymeleaf value syntax equivalent to fragment inclusions in
      `th:include` or `th:substituteby` attributes:
	  `"TEMPLATESELECTOR (:: FRAGMENTSELECTOR)?"`:
	  *   Template Selector:
	    *    Understandable by the template resolvers you configured, just as
             with any other thymeleaf template.
		*    Can use Standard Expressions: `${...}`, `*{...}`, literals,
		operands, etc. (externalized messages and links are *not allowed*).
	  *   Fragment Selector (optional): 
	    *    Can specify fragments by name (using `tiles:fragment`).
		*    Can specify XPath-like DOM Selector (`[//div[@id='content']`)
		*    Can use Standard Expressions: `${...}`, `*{...}`, literals,
		operands, etc. (externalized messages and links are *not allowed*).
  * Tiles definition wildcards (from Tiles 2.2.2) can be used.

A quick example:

    <tiles-definitions>
      ...  
      <definition name="main" template="basic_layout">
        <put-attribute name="content">
          <definition template="basic_contentlayout :: content">
            <put-attribute name="text" value="main :: text" />
          </definition>
        </put-attribute>
        <put-attribute name="side" value="${config.sideColumnTemplate}" />
      </definition>
      ...
    </tiles-definitions>

  
Inserting attributes
--------------------

The new **`tiles`** dialect allows you to insert Tiles attribute easily,
just as you'd do with `th:include`:

    <html xmlns:th="http://www.thymeleaf.org"
	      xmlns:tiles="http://www.thymeleaf.org">
      <body>
	    ...
	    <div tiles:include="side">
		   some prototyping markup over here...
		</div>
	    ...
	  </body>
	</html>

That `tiles:include` will work equivalently to the Standard Dialect's
`th:include`, only inserting a Tiles attribute by its name (as specified
in the definition file) instead of another template.

Another possibility, `tiles:substituteby`:

    <div tiles:substituteby="side">
	   some prototyping markup over here...
	</div>

...will work almost exactly as `tiles:include`, but substituting the
containing `<div>` tag by the attribute contents, instead of inserting
these contents inside it.

What about inserting *string* attributes? Easy:

    <span tiles:string="some_string_attribute">blah blah</span>

And there's one more attribute, which allows you to (optionally) specify 
which parts of your template you will be using as a fragment. So:

    <div tiles:fragment="text">
	   lorem ipsum sic dolor amet blah blah...
	</div>

Will specify a fragment you can use in your definitions with:
  
    <put-attribute name="text" value="main :: text" />

  
Mixing Thymeleaf and JSP
------------------------

For a better legacy integration and a smoother migration of applications, Thymeleaf templates and JSPs can
be mixed together in Tiles definitions, and they can even *communicate*, so that they can see the variables
that the other define:

  *   Thymeleaf attribute included into Thymeleaf template:
    *   Attribute can see all local variables (e.g. `th:with`) defined in template.
  *   Thymeleaf attribute included into JSP template:
    *   Attribute can see all variables defined (e.g. `<c:set.../>`) with at least *request scope* in the JSP.
  *   JSP attribute included into Thymeleaf template:
    *   Attribute can see all local variables (e.g. `th:with`) defined in templates as *request attributes*.
    
    
  

  
