/*
 *    Copyright 2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.nativex;

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
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/**
 * Registers hints to make a MyBatis Velocity component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisVelocityNativeConfiguration implements RuntimeHintsRegistrar {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    if (!ClassUtils.isPresent("org.mybatis.scripting.velocity.VelocityLanguageDriver", classLoader)) {
      return;
    }
    for (Class<?> type : new Class<?>[] { VelocityLanguageDriver.class, VelocityLanguageDriverConfig.class,
        ParameterMappingCollector.class, TrimDirective.class, WhereDirective.class, SetDirective.class,
        InDirective.class, RepeatDirective.class, ResourceManagerImpl.class, ClasspathResourceLoader.class,
        ResourceCacheImpl.class, ParserPoolImpl.class, UberspectImpl.class, TypeConversionHandlerImpl.class,
        StandardParser.class, Foreach.class, Include.class, Parse.class, Macro.class, Evaluate.class, Break.class,
        Define.class, Stop.class }) {
      hints.reflection().registerType(type, MEMBER_CATEGORIES);
    }
    hints.resources().registerPattern("mybatis-velocity.properties");
    hints.resources().registerPattern("org/apache/velocity/runtime/defaults/*.properties");
  }

}
