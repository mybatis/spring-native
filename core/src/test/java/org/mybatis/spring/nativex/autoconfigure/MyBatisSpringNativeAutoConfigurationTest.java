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
package org.mybatis.spring.nativex.autoconfigure;

import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.nativex.MyBatisResourcesScan;
import org.mybatis.spring.nativex.component.AbstractTypeHandler;
import org.mybatis.spring.nativex.component.BarService;
import org.mybatis.spring.nativex.component.BarTypeHandler;
import org.mybatis.spring.nativex.component.FooTypeHandler;
import org.mybatis.spring.nativex.component.TypeHandlers;
import org.mybatis.spring.nativex.component2.AnyTypeHandler;
import org.mybatis.spring.nativex.entity.City;
import org.mybatis.spring.nativex.entity.Country;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

class MyBatisSpringNativeAutoConfigurationTest {

  private AnnotationConfigApplicationContext context;

  @BeforeEach
  void init() {
    this.context = new AnnotationConfigApplicationContext();
  }

  @AfterEach
  void closeContext() {
    if (this.context != null) {
      this.context.close();
    }
  }

  @Test
  void empty() {
    this.context.register(EmptyConfiguration.class);
    this.context.refresh();
    SqlSessionFactory factory = this.context.getBean(SqlSessionFactory.class);
    Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().getTypeAliases()).hasSize(72);
    Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers()).hasSize(40);
    Assertions.assertThat(factory.getConfiguration().getMappedStatementNames()).isEmpty();
  }

  @Test
  void single() {
    this.context.register(SingleConfiguration.class);
    this.context.refresh();
    SqlSessionFactory factory = this.context.getBean(SqlSessionFactory.class);
    {
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().getTypeAliases()).hasSize(74);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("city"))
          .isEqualTo(City.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("country"))
          .isEqualTo(Country.class);
    }
    {
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers()).hasSize(43);
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(BarTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(FooTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(TypeHandlers.InnerTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getMappedStatementNames()).containsExactlyInAnyOrder(
          "sub1.AnyMapper.select", "select", "sub1.FooMapper.select", "sub1.BarMapper.select");
    }
  }

  @Test
  void singleWithoutMapperLocation() {
    this.context.register(SingleConfigurationWithoutMapperLocation.class);
    this.context.refresh();
    SqlSessionFactory factory = this.context.getBean(SqlSessionFactory.class);
    Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().getTypeAliases()).hasSize(74);
    Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers()).hasSize(43);
    Assertions.assertThat(factory.getConfiguration().getMappedStatementNames()).isEmpty();
  }

  @Test
  void multi() {
    this.context.register(MultiConfiguration.class);
    this.context.refresh();
    SqlSessionFactory factory = this.context.getBean(SqlSessionFactory.class);
    {
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().getTypeAliases()).hasSize(79);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("city"))
          .isEqualTo(City.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("country"))
          .isEqualTo(Country.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("abstracttypehandler"))
          .isEqualTo(AbstractTypeHandler.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("barservice"))
          .isEqualTo(BarService.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("bartypehandler"))
          .isEqualTo(BarTypeHandler.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("footypehandler"))
          .isEqualTo(FooTypeHandler.class);
      Assertions.assertThat(factory.getConfiguration().getTypeAliasRegistry().resolveAlias("typehandlers"))
          .isEqualTo(TypeHandlers.class);
    }
    {
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers()).hasSize(44);
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(BarTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(FooTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(TypeHandlers.InnerTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getTypeHandlerRegistry().getTypeHandlers().stream()
          .filter(AnyTypeHandler.class::isInstance).collect(Collectors.toList())).isNotEmpty();
      Assertions.assertThat(factory.getConfiguration().getMappedStatementNames()).containsExactlyInAnyOrder(
          "sub1.AnyMapper.select", "select", "sub1.FooMapper.select", "sub1.BarMapper.select");
    }
  }

  @EnableAutoConfiguration
  @Configuration
  static class EmptyConfiguration {

  }

  @EnableAutoConfiguration
  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", typeHandlerPackages = "org.mybatis.spring.nativex.component", mapperLocationPatterns = "mapper/**/*.xml")
  @Configuration
  static class SingleConfiguration {

  }

  @EnableAutoConfiguration
  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @Configuration
  static class SingleConfigurationWithoutMapperLocation {

  }

  @EnableAutoConfiguration
  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity")
  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.component")
  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component2")
  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub1/*.xml")
  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub2/*.xml")
  @Configuration
  static class MultiConfiguration {

  }

}
