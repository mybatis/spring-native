/*
 *    Copyright 2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.nativex;

import static org.springframework.nativex.hint.TypeAccess.DECLARED_CLASSES;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_FIELDS;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_METHODS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_CLASSES;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_FIELDS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_METHODS;

import org.apache.velocity.runtime.ParserPoolImpl;
import org.apache.velocity.runtime.directive.Break;
import org.apache.velocity.runtime.directive.Define;
import org.apache.velocity.runtime.directive.Evaluate;
import org.apache.velocity.runtime.directive.Foreach;
import org.apache.velocity.runtime.directive.Include;
import org.apache.velocity.runtime.directive.Macro;
import org.apache.velocity.runtime.directive.Parse;
import org.apache.velocity.runtime.directive.Stop;
import org.apache.velocity.runtime.parser.StandardParser;
import org.apache.velocity.runtime.resource.ResourceCacheImpl;
import org.apache.velocity.runtime.resource.ResourceManagerImpl;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.util.introspection.TypeConversionHandlerImpl;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.mybatis.scripting.velocity.InDirective;
import org.mybatis.scripting.velocity.ParameterMappingCollector;
import org.mybatis.scripting.velocity.RepeatDirective;
import org.mybatis.scripting.velocity.SetDirective;
import org.mybatis.scripting.velocity.TrimDirective;
import org.mybatis.scripting.velocity.VelocityLanguageDriver;
import org.mybatis.scripting.velocity.VelocityLanguageDriverConfig;
import org.mybatis.scripting.velocity.WhereDirective;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * Registers hints to make a MyBatis Velocity component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
// @formatter:off
@NativeHint(
    trigger = VelocityLanguageDriver.class,
    resources = @ResourceHint(
        patterns = {
            "mybatis-velocity.properties",
            "org/apache/velocity/runtime/defaults/.*.properties"
        }
    )
)
@TypeHint(
    types = {
        VelocityLanguageDriver.class,
        VelocityLanguageDriverConfig.class,
        ParameterMappingCollector.class,
        TrimDirective.class,
        WhereDirective.class,
        SetDirective.class,
        InDirective.class,
        RepeatDirective.class,
        ResourceManagerImpl.class,
        ClasspathResourceLoader.class,
        ResourceCacheImpl.class,
        ParserPoolImpl.class,
        UberspectImpl.class,
        TypeConversionHandlerImpl.class,
        StandardParser.class,
        Foreach.class,
        Include.class,
        Parse.class,
        Macro.class,
        Evaluate.class,
        Break.class,
        Define.class,
        Stop.class
    },
    access = {
        PUBLIC_CONSTRUCTORS,
        PUBLIC_CLASSES,
        PUBLIC_FIELDS,
        PUBLIC_METHODS,
        DECLARED_CLASSES,
        DECLARED_CONSTRUCTORS,
        DECLARED_FIELDS,
        DECLARED_METHODS
    }
)
// @formatter:on
public class MyBatisVelocityNativeConfiguration implements NativeConfiguration {
}
