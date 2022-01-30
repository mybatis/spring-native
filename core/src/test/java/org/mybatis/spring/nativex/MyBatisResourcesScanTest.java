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

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.nativex.component.AbstractTypeHandler;
import org.mybatis.spring.nativex.component.BarService;
import org.mybatis.spring.nativex.component.BarTypeHandler;
import org.mybatis.spring.nativex.component.FooTypeHandler;
import org.mybatis.spring.nativex.component.TypeHandlers;
import org.mybatis.spring.nativex.component2.AnyTypeHandler;
import org.mybatis.spring.nativex.entity.City;
import org.mybatis.spring.nativex.entity.Country;
import org.mybatis.spring.nativex.marker.StandardEntity;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.TypeAccess;

class MyBatisResourcesScanTest {

  @Test
  void scanTypeAliases() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanTypeAliases.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).containsExactlyInAnyOrder(City.class, Country.class);
    Assertions.assertThat(holder.getMapperLocations()).isEmpty();
    Assertions.assertThat(holder.getTypeHandlerClasses()).isEmpty();
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(City.class, Country.class);
    Assertions.assertThat(holder.getResourceLocations()).isEmpty();
    Assertions.assertThat(holder.getReflectionTypeAccesses()).isEmpty();
  }

  @Test
  void scanTypeAliasesWithSupperType() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanTypeAliasesWithSupperType.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).containsExactlyInAnyOrder(Country.class);
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(Country.class);
  }

  @Test
  void scanTypeAliasesWithMultiPackage() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanTypeAliasesWithMultiPackage.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).containsExactlyInAnyOrder(City.class, Country.class,
        BarTypeHandler.class, FooTypeHandler.class, BarService.class, AbstractTypeHandler.class, TypeHandlers.class);
    Assertions.assertThat(holder.getMapperLocations()).isEmpty();
    Assertions.assertThat(holder.getTypeHandlerClasses()).isEmpty();
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(City.class, Country.class,
        BarTypeHandler.class, FooTypeHandler.class, BarService.class, AbstractTypeHandler.class, TypeHandlers.class);
    Assertions.assertThat(holder.getResourceLocations()).isEmpty();
    Assertions.assertThat(holder.getReflectionTypeAccesses()).isEmpty();
  }

  @Test
  void scanTypeHandler() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanTypeHandler.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).isEmpty();
    Assertions.assertThat(holder.getMapperLocations()).isEmpty();
    Assertions.assertThat(holder.getTypeHandlerClasses()).containsExactlyInAnyOrder(FooTypeHandler.class,
        BarTypeHandler.class, TypeHandlers.InnerTypeHandler.class);
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(FooTypeHandler.class,
        BarTypeHandler.class, TypeHandlers.InnerTypeHandler.class);
    Assertions.assertThat(holder.getResourceLocations()).isEmpty();
    Assertions.assertThat(holder.getReflectionTypeAccesses()).isEmpty();
  }

  @Test
  void scanTypeHandlerWithMultiPackage() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanTypeHandlerWithMultiPackage.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeHandlerClasses()).containsExactlyInAnyOrder(FooTypeHandler.class,
        BarTypeHandler.class, AnyTypeHandler.class, TypeHandlers.InnerTypeHandler.class);
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(FooTypeHandler.class,
        BarTypeHandler.class, AnyTypeHandler.class, TypeHandlers.InnerTypeHandler.class);
  }

  @Test
  void scanMapperLocations() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanMapperLocations.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).isEmpty();
    Assertions.assertThat(holder.getMapperLocations()).containsExactlyInAnyOrder("mapper/sub1/BarMapper.xml",
        "mapper/sub1/FooMapper.xml");
    Assertions.assertThat(holder.getTypeHandlerClasses()).isEmpty();
    Assertions.assertThat(holder.getReflectionClasses()).isEmpty();
    Assertions.assertThat(holder.getResourceLocations()).containsExactlyInAnyOrder("mapper/sub1/BarMapper.xml",
        "mapper/sub1/FooMapper.xml");
    Assertions.assertThat(holder.getReflectionTypeAccesses()).isEmpty();
  }

  @Test
  void scanMapperLocationsWithMultiPattern() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanMapperLocationsWithMultiPattern.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getMapperLocations()).containsExactlyInAnyOrder("mapper/sub2/AnyMapper.xml",
        "org/apache/ibatis/builder/xml/mybatis-3-config.dtd", "org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd");
    Assertions.assertThat(holder.getResourceLocations()).containsExactlyInAnyOrder("mapper/sub2/AnyMapper.xml",
        "org/apache/ibatis/builder/xml/mybatis-3-config.dtd", "org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd");
  }

  @Test
  void scanReflectionType() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanReflectionType.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).isEmpty();
    Assertions.assertThat(holder.getMapperLocations()).isEmpty();
    Assertions.assertThat(holder.getTypeHandlerClasses()).isEmpty();
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(City.class, Country.class);
    Assertions.assertThat(holder.getResourceLocations()).isEmpty();
    Assertions.assertThat(holder.getReflectionTypeAccesses()).containsExactlyInAnyOrder(TypeAccess.DECLARED_CLASSES);
  }

  @Test
  void scanReflectionTypeWithSupperType() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanReflectionTypeWithSuperType.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(Country.class);
  }

  @Test
  void scanReflectionTypeWithMultiPackage() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanReflectionTypeWithMultiPackage.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getReflectionClasses()).containsExactlyInAnyOrder(City.class, Country.class,
        BarService.class, BarTypeHandler.class, FooTypeHandler.class, TypeHandlers.class);
  }

  @Test
  void scanResourceLocations() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanResourceLocations.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getTypeAliasesClasses()).isEmpty();
    Assertions.assertThat(holder.getMapperLocations()).isEmpty();
    Assertions.assertThat(holder.getTypeHandlerClasses()).isEmpty();
    Assertions.assertThat(holder.getReflectionClasses()).isEmpty();
    Assertions.assertThat(holder.getResourceLocations()).containsExactlyInAnyOrder("mapper/sub1/BarMapper.xml",
        "mapper/sub1/FooMapper.xml");
    Assertions.assertThat(holder.getReflectionTypeAccesses()).isEmpty();
  }

  @Test
  void scanResourceLocationsWithMultiPattern() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForScanResourceLocationsWithMultiPattern.class);
    context.refresh();
    MyBatisScannedResourcesHolder holder = context.getBean(MyBatisScannedResourcesHolder.class);
    Assertions.assertThat(holder.getResourceLocations()).containsExactlyInAnyOrder("mapper/sub2/AnyMapper.xml",
        "org/apache/ibatis/builder/xml/mybatis-3-config.dtd", "org/apache/ibatis/builder/xml/mybatis-3-mapper.dtd");
  }

  @Test
  void scanRepeat() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForRepeat.class);
    context.refresh();
    Set<MyBatisScannedResourcesHolder> list = Stream
        .of(context.getBeanNamesForType(MyBatisScannedResourcesHolder.class))
        .map(x -> context.getBean(x, MyBatisScannedResourcesHolder.class)).collect(Collectors.toSet());
    Set<Class<?>> typeAliasesClasses = list.stream().flatMap(x -> x.getTypeAliasesClasses().stream())
        .collect(Collectors.toSet());
    Set<Class<?>> typeHandlerClasses = list.stream().flatMap(x -> x.getTypeHandlerClasses().stream())
        .collect(Collectors.toSet());
    Set<Class<?>> reflectionClasses = list.stream().flatMap(x -> x.getReflectionClasses().stream())
        .collect(Collectors.toSet());
    Set<String> mapperLocations = list.stream().flatMap(x -> x.getMapperLocations().stream())
        .collect(Collectors.toSet());
    Set<String> resourceLocations = list.stream().flatMap(x -> x.getResourceLocations().stream())
        .collect(Collectors.toSet());
    Assertions.assertThat(typeAliasesClasses).containsExactlyInAnyOrder(Country.class, City.class);
    Assertions.assertThat(typeHandlerClasses).containsExactlyInAnyOrder(FooTypeHandler.class, BarTypeHandler.class,
        TypeHandlers.InnerTypeHandler.class);
    Assertions.assertThat(reflectionClasses).containsExactlyInAnyOrder(Country.class, City.class, FooTypeHandler.class,
        BarTypeHandler.class, AnyTypeHandler.class, TypeHandlers.InnerTypeHandler.class);
    Assertions.assertThat(mapperLocations).containsExactlyInAnyOrder("mapper/sub1/FooMapper.xml",
        "mapper/sub1/BarMapper.xml");
    Assertions.assertThat(resourceLocations).containsExactlyInAnyOrder("mapper/sub1/FooMapper.xml",
        "mapper/sub2/AnyMapper.xml", "mapper/sub1/BarMapper.xml");
  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity")
  @Configuration
  static class ConfigurationForScanTypeAliases {
  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", typeAliasesSupperType = StandardEntity.class)
  @Configuration
  static class ConfigurationForScanTypeAliasesWithSupperType {
  }

  @MyBatisResourcesScan(typeAliasesPackages = { "org.mybatis.spring.nativex.entity",
      "org.mybatis.spring.nativex.component" })
  @Configuration
  static class ConfigurationForScanTypeAliasesWithMultiPackage {
  }

  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @Configuration
  static class ConfigurationForScanTypeHandler {
  }

  @MyBatisResourcesScan(typeHandlerPackages = { "org.mybatis.spring.nativex.component",
      "org.mybatis.spring.nativex.component2" })
  @Configuration
  static class ConfigurationForScanTypeHandlerWithMultiPackage {
  }

  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub1/*.*")
  @Configuration
  static class ConfigurationForScanMapperLocations {
  }

  @MyBatisResourcesScan(mapperLocationPatterns = { "mapper/sub2/*.*", "org/apache/ibatis/builder/xml/*.dtd" })
  @Configuration
  static class ConfigurationForScanMapperLocationsWithMultiPattern {
  }

  @MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.entity", typeAccesses = TypeAccess.DECLARED_CLASSES)
  @Configuration
  static class ConfigurationForScanReflectionType {
  }

  @MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.entity", reflectionTypeSupperType = StandardEntity.class, typeAccesses = TypeAccess.DECLARED_CLASSES)
  @Configuration
  static class ConfigurationForScanReflectionTypeWithSuperType {
  }

  @MyBatisResourcesScan(reflectionTypePackages = { "org.mybatis.spring.nativex.entity",
      "org.mybatis.spring.nativex.component" })
  @Configuration
  static class ConfigurationForScanReflectionTypeWithMultiPackage {
  }

  @MyBatisResourcesScan(resourceLocationPatterns = "mapper/sub1/*.*")
  @Configuration
  static class ConfigurationForScanResourceLocations {
  }

  @MyBatisResourcesScan(resourceLocationPatterns = { "mapper/sub2/*.*", "org/apache/ibatis/builder/xml/*.dtd" })
  @Configuration
  static class ConfigurationForScanResourceLocationsWithMultiPattern {
  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity")
  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub1/*.*")
  @MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.component2")
  @MyBatisResourcesScan(resourceLocationPatterns = "mapper/sub2/*.*")
  @Configuration
  static class ConfigurationForRepeat {

  }

}
