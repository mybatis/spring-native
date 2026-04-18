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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.nativex.component.BarTypeHandler;
import org.mybatis.spring.nativex.component.FooTypeHandler;
import org.mybatis.spring.nativex.component.TypeHandlers;
import org.mybatis.spring.nativex.component2.AnyTypeHandler;
import org.mybatis.spring.nativex.entity.City;
import org.mybatis.spring.nativex.entity.Country;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

class MyBatisScannedResourcesNativeConfigurationProcessorTest {

  private static final MemberCategory[] DEFAULT_MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Test
  void empty() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.refresh();
    RuntimeHints hints = process(context.getDefaultListableBeanFactory());
    assertThat(hints.reflection().typeHints()).isEmpty();
    assertThat(hints.resources().resourcePatternHints()).isEmpty();
  }

  @Test
  void one() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForOne.class);
    context.refresh();
    RuntimeHints hints = process(context.getDefaultListableBeanFactory());
    // reflection hint
    {
      assertThat(hints.reflection().typeHints()).hasSize(2);
      assertThat(RuntimeHintsPredicates.reflection().onType(City.class)
          .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
              .accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(Country.class)
          .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
              .accepts(hints);
    }
    // resource hint
    {
      assertThat(RuntimeHintsPredicates.resource().forResource("mapper/sub1/BarMapper.xml")).accepts(hints);
      assertThat(RuntimeHintsPredicates.resource().forResource("mapper/sub1/FooMapper.xml")).accepts(hints);
    }
  }

  @Test
  void multi() {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(ConfigurationForMulti.class);
    context.refresh();
    RuntimeHints hints = process(context.getDefaultListableBeanFactory());
    // reflection hint
    {
      assertThat(hints.reflection().typeHints()).hasSize(6);
      assertThat(RuntimeHintsPredicates.reflection().onType(City.class)
          .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
              .accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(Country.class)
          .withMemberCategories(MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS))
              .accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(FooTypeHandler.class)
          .withMemberCategories(DEFAULT_MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(BarTypeHandler.class)
          .withMemberCategories(DEFAULT_MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(AnyTypeHandler.class)
          .withMemberCategories(DEFAULT_MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(TypeHandlers.InnerTypeHandler.class)
          .withMemberCategories(DEFAULT_MEMBER_CATEGORIES)).accepts(hints);
    }
    // resource hint
    {
      assertThat(RuntimeHintsPredicates.resource().forResource("mapper/sub1/BarMapper.xml")).accepts(hints);
      assertThat(RuntimeHintsPredicates.resource().forResource("mapper/sub1/FooMapper.xml")).accepts(hints);
      assertThat(RuntimeHintsPredicates.resource().forResource("mapper/sub2/AnyMapper.xml")).accepts(hints);
    }
  }

  private RuntimeHints process(DefaultListableBeanFactory beanFactory) {
    RuntimeHints hints = new RuntimeHints();
    GenerationContext generationContext = mock(GenerationContext.class);
    when(generationContext.getRuntimeHints()).thenReturn(hints);
    BeanFactoryInitializationAotContribution contribution = new MyBatisScannedResourcesNativeConfigurationProcessor()
        .processAheadOfTime(beanFactory);
    if (contribution != null) {
      contribution.applyTo(generationContext, mock(BeanFactoryInitializationCode.class));
    }
    return hints;
  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", mapperLocationPatterns = "mapper/sub1/*.*", typeAccesses = {
      MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS })
  @Configuration
  static class ConfigurationForOne {

  }

  @MyBatisResourcesScan(typeAliasesPackages = "org.mybatis.spring.nativex.entity", typeAccesses = {
      MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS })
  @MyBatisResourcesScan(typeHandlerPackages = "org.mybatis.spring.nativex.component")
  @MyBatisResourcesScan(mapperLocationPatterns = "mapper/sub1/*.*")
  @MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.component2")
  @MyBatisResourcesScan(resourceLocationPatterns = "mapper/sub2/*.*")
  @Configuration
  static class ConfigurationForMulti {

  }

}
