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
package org.thymeleaf.extras.tiles2.spring.web.factory;

import java.lang.reflect.Field;

import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.preparer.PreparerFactory;
import org.springframework.web.servlet.view.tiles2.TilesConfigurer;
import org.thymeleaf.exceptions.ConfigurationException;




/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 2.0.9
 *
 */
final class TilesConfigurerSuperClassIntegration {
    
    static final String USE_MUTABLE_TILES_CONTAINER_PROPERTY = "useMutableTilesContainer";
    static final String DEFINITIONS_PROPERTY = "definitions";
    static final String CHECK_REFRESH_PROPERTY = "checkRefresh";
    static final String VALIDATE_DEFINITIONS_PROPERTY = "validateDefinitions";
    static final String DEFINITIONS_FACTORY_CLASS_PROPERTY = "definitionsFactoryClass";
    static final String PREPARER_FACTORY_CLASS_PROPERTY = "preparerFactoryClass";


    
    static Object accessSuperClassProperty(final TilesConfigurer tilesConfigurer, final String name) {
        
        try {
            
            final Field field = TilesConfigurer.class.getDeclaredField(name);
            field.setAccessible(true);
            final Object value = field.get(tilesConfigurer);
            field.setAccessible(false);
            return value;
            
        } catch (final Exception e) {
            throw new ConfigurationException("Cannot access field \"" + name + "\"  in " +
                        "class " + tilesConfigurer.getClass().getName() + ". Maybe the " +
                        "implementation of its superclass has changed and the current " +
                        "integration facilities are is no longer compatible.", e);
        }
        
    }
    
    
    
    static boolean accessSuperClassBooleanProperty(final TilesConfigurer tilesConfigurer, final String name) {
        
        final Object objResult = accessSuperClassProperty(tilesConfigurer, name);
        if (objResult == null) {
            throw new ConfigurationException("Cannot access field \"" + name + "\"  in " +
                    "class " + tilesConfigurer.getClass().getName() + " (returned null). Maybe the " +
                    "implementation of its superclass has changed and the current " +
                    "integration facilities are is no longer compatible.");
        }
        if (!(objResult instanceof Boolean)) {
            throw new ConfigurationException("Cannot access boolean field \"" + name + "\"  in " +
                    "class " + tilesConfigurer.getClass().getName() + ". Maybe the " +
                    "implementation of its superclass has changed and the current " +
                    "integration facilities are is no longer compatible.");
        }
        
        final Boolean result = (Boolean) objResult;
        return result.booleanValue();
        
    }
    
    
    
    static Class<?> accessSuperClassClassProperty(final TilesConfigurer tilesConfigurer, final String name) {
        
        final Object objResult = accessSuperClassProperty(tilesConfigurer, name);
        if (objResult == null) {
            return null;
        }
        if (!(objResult instanceof Class)) {
            throw new ConfigurationException("Cannot access class-typed field \"" + name + "\"  in " +
                    "class " + tilesConfigurer.getClass().getName() + ". Maybe the " +
                    "implementation of its superclass has changed and the current " +
                    "integration facilities are is no longer compatible.");
        }
        
        return (Class<?>) objResult;
        
    }
    
    
    
    static String[] accessSuperClassStringArrayProperty(final TilesConfigurer tilesConfigurer, final String name) {
        
        final Object objResult = accessSuperClassProperty(tilesConfigurer, name);
        if (objResult == null) {
            return null;
        }
        if (!(objResult instanceof String[])) {
            throw new ConfigurationException("Cannot access String[] field \"" + name + "\"  in " +
                    "class " + tilesConfigurer.getClass().getName() + ". Maybe the " +
                    "implementation of its superclass has changed and the current " +
                    "integration facilities are is no longer compatible.");
        }
        
        return (String[]) objResult;
        
    }
    

    
    
    
    static boolean getUseMutableTilesContainer(final TilesConfigurer tilesConfigurer) {
        return accessSuperClassBooleanProperty(tilesConfigurer, USE_MUTABLE_TILES_CONTAINER_PROPERTY);
    }
    

    
    static boolean getCheckRefresh(final TilesConfigurer tilesConfigurer) {
        return accessSuperClassBooleanProperty(tilesConfigurer, CHECK_REFRESH_PROPERTY);
    }
    
    
    
    static boolean getValidateDefinitions(final TilesConfigurer tilesConfigurer) {
        return accessSuperClassBooleanProperty(tilesConfigurer, VALIDATE_DEFINITIONS_PROPERTY);
    }
    
    
    
    @SuppressWarnings("unchecked")
    static Class<? extends DefinitionsFactory> getDefinitionsFactoryClass(final TilesConfigurer tilesConfigurer) {
        return (Class<? extends DefinitionsFactory>) accessSuperClassClassProperty(tilesConfigurer, DEFINITIONS_FACTORY_CLASS_PROPERTY);
    }
    
    
    
    @SuppressWarnings("unchecked")
    static Class<? extends PreparerFactory> getPreparerFactoryClass(final TilesConfigurer tilesConfigurer) {
        return (Class<? extends PreparerFactory>) accessSuperClassClassProperty(tilesConfigurer, PREPARER_FACTORY_CLASS_PROPERTY);
    }
    
    
    
    static String[] getDefinitions(final TilesConfigurer tilesConfigurer) {
        return accessSuperClassStringArrayProperty(tilesConfigurer, DEFINITIONS_PROPERTY);
    }
    

   
    
    

    
    
    private TilesConfigurerSuperClassIntegration() {
        super();
    }
   
}
