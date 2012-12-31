
Thymeleaf - Tiles 2 integration module
======================================

------------------------------------------------------------------------------

Status
------

This is a *thymeleaf extras* module, not a part of the Thymeleaf core (and as
such following its own versioning schema), but fully supported by the 
Thymeleaf team.

Current versions: 

  * **Version 2.0.0** - for Thymeleaf 2.0 (2.0.14+) 


License
-------

This software is licensed under the [Apache License 2.0]
(http://www.apache.org/licenses/LICENSE-2.0.html).


Requirements
------------

  *   Thymeleaf **2.0.14+**
  *   Apache Tiles 2 version **2.2.1+** (**2.2.2** recommended)
  *   Web environment (Tiles integration cannot work offline)


Maven info
----------

  *   groupId: `org.thymeleaf.extras`   
  *   artifactId: `thymeleaf-extras-tiles2`


Features
--------

  *   *(Optional)* **Spring MVC 3** integration
  *   *(Optional)* **Spring Web Flow 2.3** integration, with AJAX fragment 
      rendering support.
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
we will first need to configure our application in the usual way for
Spring + Thymeleaf applications (*TemplateEngine* bean, *template resolvers*, 
etc.), and then create an instance of the `ThymeleafTilesConfigurer` (similar
to the Spring Tiles configurer for JSP), like:

```xml
    <bean id="tilesConfigurer" class="org.thymeleaf.extras.tiles2.spring.web.configurer.ThymeleafTilesConfigurer">
      <property name="definitions">
        <list>
          <value>/WEB-INF/tiles-defs.xml</value>
        </list>
      </property>
    </bean>
```

Also, we will need to configure our Thymeleaf *view resolver* in order to
use a specialized Thymeleaf-Tiles2 view class:

```xml
    <bean id="tilesViewResolver" class="org.thymeleaf.spring3.view.ThymeleafViewResolver">
      <property name="viewClass" value="org.thymeleaf.extras.tiles2.spring.web.view.ThymeleafTilesView"/>
      <property name="templateEngine" ref="templateEngine" />
    </bean>
```

...and finally, add the Tiles dialect to our Template Engine so that we
can use the `tiles:*` attributes:

```xml
    <bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine">
      ...
      <property name="additionalDialects">
        <set>
          <bean class="org.thymeleaf.extras.tiles2.dialect.TilesDialect"/>
        </set>
      </property>
	  ...
    </bean>
```

And that's all! Now we can make our controller methods return Tiles 
definition names as view names and everything should work fine.



Configuration and usage without Spring
--------------------------------------

Following the standard configuration mechanisms in Tiles 2.2, in order to 
use Thymeleaf + Tiles in a non-Spring application, we should:

  *   Either configure an `org.thymeleaf.extras.tiles2.web.startup.ThymeleafTilesListener`
      at our `web.xml`
  *   ...or configure an `org.thymeleaf.extras.tiles2.web.startup.ThymeleafTilesServlet`,
      also at our `web.xml`.

Both these artifacts declare and initialize a Thymeleaf-enabled 
`TilesContainer` instance, which we can access with:

```java
    final TilesContainer tiles = ServletUtil.getContainer(servletContext);
```
    
...and then execute, specifying the definition to be executed and the 
following sequence of *request items*:

  1.   The Thymeleaf *template engine* (`TemplateEngine`)
  2.   The Thymeleaf *context* (`IContext`)
  3.   The `HttpServletRequest`
  4.   The `HttpServletResponse`
  5.   The `java.io.Writer` the result should be written to (for example, 
       `response.getWriter()`)

Put as code:
  
```java
    tiles.render("myDefinition", templateEngine, ctx, request, response, writer);
```
  
  
Using Thymeleaf in our definition files
---------------------------------------

Using Thymeleaf in our definition files (usually called something like
`tiles-defs.xml`) is easy. These are the key points:
  
  *   Thymeleaf is now the default *template type*, instead of JSP.
    *   We can use `templateType="thymeleaf"` or simply omit `teamplateType`
        for our templates, and `type="thymeleaf"` for attributes.
	*   We can use `templateType="jsp"` for our JSP templates
	    (`type="jsp"` for our attributes).
  *   Thymeleaf value syntax is equivalent to that of  `th:include` and
      `th:substituteby` attributes:
	  `"TEMPLATESELECTOR (:: FRAGMENTSELECTOR)?"`
	  *   Template Selector:
	    *    Understandable by the template resolvers you configured, just as
             with any other thymeleaf templates.
		*    Can use Standard Expressions: `${...}`, `*{...}`, literals,
		operands, etc. (externalized messages and links are *not allowed*).
	  *   Fragment Selector (optional): 
	    *    Can specify fragments by name (using attribute `tiles:fragment`).
		*    Can specify XPath-like DOM Selector (`[//div[@id='content']`)
		*    Can use Standard Expressions: `${...}`, `*{...}`, literals,
		operands, etc. (externalized messages and links are *not allowed*).
  * Tiles definition wildcards (from Tiles 2.2.2) can be used.

A quick example:

```xml
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
```

  
Inserting attributes
--------------------

The new `tiles` dialect allows us to insert Tiles attributes easily,
just as we'd do with `th:include`:

```xml
    <html xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">
      ...
      <body>
	    ...
	    <div tiles:include="side">
		   some prototyping markup over here...
		</div>
	    ...
	  </body>
	</html>
```

That `tiles:include` will work equivalently to the Standard Dialect's
`th:include`, only inserting a Tiles attribute by its name (as specified
in the definition file) instead of another template.

Another possibility, `tiles:substituteby`:

```xml
    <div tiles:substituteby="side">
	   some prototyping markup over here...
	</div>
```

...will work almost exactly as `tiles:include`, but substituting the
containing `<div>` tag by the attribute contents, instead of inserting
these contents inside it.

What about inserting *string* attributes? Easy:

```xml
    <span tiles:string="some_string_attribute">blah blah</span>
```

And there's one more attribute, which allows us to (optionally) specify 
which part of our template we will be using as a fragment. So:

```xml
    <div tiles:fragment="text">
	   lorem ipsum sic dolor amet blah blah...
	</div>
```

...will specify a fragment we can use in our definitions with:
  
```xml
    <put-attribute name="text" value="main :: text" />
```

  
Mixing Thymeleaf and JSP
------------------------

For a better legacy integration and a smoother migration of applications,
Thymeleaf templates and JSPs can be mixed together in Tiles definitions, so
that Thymeleaf templates can include JSP attributes, and viceversa.

And these fragments in different technologies can even *communicate*, so 
that they can see the variables that the others define:

  *   Thymeleaf attribute included into Thymeleaf template:
    *   Attribute can see all local variables (e.g. `th:with`) defined in 
        template.
  *   Thymeleaf attribute included into JSP template:
    *   Attribute can see all variables defined (e.g. `<c:set.../>`) with
        at least *request scope* in the JSP.
  *   JSP attribute included into Thymeleaf template:
    *   Attribute can see all local variables (e.g. `th:with`) defined in
        templates as *request attributes*.
    
    

  
Using Spring Web Flow
---------------------

In order to use Thymeleaf + Tiles in our Spring Web Flow applications, we
will first need to modify the definition of our *View Resolver*, so that it
uses thymeleaf's AJAX-enabled implementation configured to create *View*
instances that understand the SpringWebFlow-Tiles combination:
  

```xml
    <bean id="tilesViewResolver" class="org.thymeleaf.spring3.view.AjaxThymeleafViewResolver">
      <property name="viewClass" value="org.thymeleaf.extras.tiles2.spring.web.view.FlowAjaxThymeleafTilesView"/>
      <property name="templateEngine" ref="templateEngine" />
    </bean>
```


### Scenario


Let's say we have a Tiles definition like:

```xml
    ...
    <definition name="order_details" template="orderdet">
      <put-attribute name="prog_content" value="progress :: progtext" />
    </definition>
	...
```

Which references a *template file* called `orderdet`, which will probably be added
a prefix and a suffix by the Thymeleaf *template resolver*, resulting into something
like `/WEB-INF/templates/orderdet.html`. 

Also, we can see how `orderdet` includes a fragment (an *attribute* in Tiles jargon)
called `prog_content`, which resolves to a fragment (specified with `tiles:fragment`)
called `progtext` in template `progress` (probably `/WEB-INF/templates/progress.html`).

Let's see how these two template files could look like. First, the relevant parts
of `orderdet.html`:

```html
    <!DOCTYPE html>

    <html xmlns:th="http://www.thymeleaf.org" xmlns:tiles="http://www.thymeleaf.org">
    
      <head>
        ...
        <script type="text/javascript" th:src="@{/resources/dojo/dojo.js}"></script>
        <script type="text/javascript" th:src="@{/resources/spring/Spring.js}"></script>
        <script type="text/javascript" th:src="@{/resources/spring/Spring-Dojo.js}"></script>
      </head>
    
      <body>
    
        ...
        ...
    
        <div tiles:substituteby="prog_content">
    	  Progress of your order: GOOD!
    	</div>
    	
        <form id="progressForm" method="post">
          <input type="submit" id="progressButton" name="_eventId_checkprogress" value="Update order progress!" />
        </form>
    
    
        <script type="text/javascript">
            Spring.addDecoration(
                    new Spring.AjaxEventDecoration(
                            {
                             elementId:'progressButton',
                             event:'onclick',
                             formId:'progressForm'
                            }));
        </script>
    
      </body>
      
    </html>
```


There you can see how we added the required JavaScript libraries from the Spring JS project
(a part of Spring Web Flow), and then linked an AJAX call for Spring Web Flow's
event `checkprogress` to our form button.

The relevant parts of our `progress.html` file could look like:   


```html
    ...
    <div id="progressText" tiles:fragment="progtext">
      Progress of your order <span th:text="${order.percentComplete}">34</span>%
    </div>
	...
```

Note that, in order for Spring Web Flow to be able to refresh a markup fragment via AJAX, 
the specific fragment must be completely enclosed in an element specifying an `id` attribute, 
like the `<div id="progressText" ...>` you see above. Also note that in order to preserve 
that `id` when it renders, we use `tiles:substituteby` instead of `tiles:include`.


Our flow can now define a transition for updating that fragment as a part of 
the *Order Details* view (`view-state`): 

```xml
    <view-state id="orderdetailsview" view="orderdet">
        <transition on="checkprogress">
          <render fragments="prog_content"/>
        </transition>
    </view-state>
```

Your AJAX interactions should now work seamlessly.



