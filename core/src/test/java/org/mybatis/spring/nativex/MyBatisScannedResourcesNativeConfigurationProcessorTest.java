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

import static org.springframework.nativex.hint.TypeAccess.*;
import static org.springframework.nativex.hint.TypeAccess.QUERY_PUBLIC_CONSTRUCTORS;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.nativex.component.BarTypeHandler;
import org.mybatis.spring.nativex.component.FooTypeHandler;
import org.mybatis.spring.nativex.component.TypeHandlers;
import org.mybatis.spring.nativex.component2.AnyTypeHandler;
import org.mybatis.spring.nativex.entity.City;
import org.mybatis.spring.nativex.entity.Country;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.DefaultNativeReflectionEntry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.TypeAccess;

class MyBatisScannedResourcesNativeConfigurationProcessorTest {

  private static final TypeAccess[] DEFAULT_TYPE_ACCESSES = { PUBLIC_CONSTRUCTORS, PUBLIC_CLASSES, PUBLIC_FIELDS,
      PUBLIC_METHODS, DECLARED_CLASSES, DECLARED_CONSTRUCTORS, DECLARED_FIELDS, DECLARED_METHODS,
      QUERY_DECLARED_METHODS, QUERY_PUBLIC_METHODS, QUERY_DECLARED_CONSTRUCTORS, QUERY_PUBLIC_CONSTRUCTORS };

  @Test
  void empty() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.refresh();
    NativeConfigurationRegistry registry = process(context.getDefaultListableBeanFactory());
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).isEmpty();
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).isEmpty();
    }
  }

  @Test
  void one() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForOne.class);
    context.refresh();
    NativeConfigurationRegistry registry = process(context.getDefaultListableBeanFactory());
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).hasSize(2);
      Assertions.assertThat(entries.get(City.class)).satisfies(x -> Assertions.assertThat(x.getAccess().toArray())
          .isEqualTo(new TypeAccess[] { PUBLIC_CONSTRUCTORS, PUBLIC_METHODS }));
      Assertions.assertThat(entries.get(Country.class)).satisfies(x -> Assertions.assertThat(x.getAccess().toArray())
          .isEqualTo(new TypeAccess[] { PUBLIC_CONSTRUCTORS, PUBLIC_METHODS }));
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).containsExactlyInAnyOrder("mapper/sub1/BarMapper.xml",
          "mapper/sub1/FooMapper.xml");
    }
  }

  @Test
  void multi() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForMulti.class);
    context.refresh();
    NativeConfigurationRegistry registry = process(context.getDefaultListableBeanFactory());
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).hasSize(6);
      Assertions.assertThat(entries.get(City.class)).satisfies(x -> Assertions.assertThat(x.getAccess().toArray())
          .isEqualTo(new TypeAccess[] { PUBLIC_CONSTRUCTORS, PUBLIC_METHODS }));
      Assertions.assertThat(entries.get(Country.class)).satisfies(x -> Assertions.assertThat(x.getAccess().toArray())
          .isEqualTo(new TypeAccess[] { PUBLIC_CONSTRUCTORS, PUBLIC_METHODS }));
      Assertions.assertThat(entries.get(FooTypeHandler.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(DEFAULT_TYPE_ACCESSES));
      Assertions.assertThat(entries.get(BarTypeHandler.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(DEFAULT_TYPE_ACCESSES));
      Assertions.assertThat(entries.get(AnyTypeHandler.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(DEFAULT_TYPE_ACCESSES));
      Assertions.assertThat(entries.get(TypeHandlers.InnerTypeHandler.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(DEFAULT_TYPE_ACCESSES));
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).containsExactlyInAnyOrder("mapper/sub1/BarMapper.xml",
          "mapper/sub1/FooMapper.xml", "mapper/sub2/AnyMapper.xml");
    }

  }

  private NativeConfigurationRegistry process(DefaultListableBeanFactory beanFactory) {
    NativeConfigurationRegistry registry = new NativeConfigurationRegistry();
    new MyBatisScannedResourcesNativeConfigurationProcessor().process(beanFactory, registry);
    return registry;
  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", mapperLocationPatterns = "mapper/sub1/*.*", typeAccesses = {
      TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS })
  @Configuration
  static class ConfigurationForOne {

  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", typeAccesses = {
      TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS })
  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub1/*.*")
  @MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.component2")
  @MyBatisResourcesScan(resourceLocationPatterns = "mapper/sub2/*.*")
  @Configuration
  static class ConfigurationForMulti {

  }

}
