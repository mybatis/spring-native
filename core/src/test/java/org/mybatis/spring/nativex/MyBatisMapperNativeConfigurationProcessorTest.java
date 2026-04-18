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
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.nativex.mapper.Sample2Mapper;
import org.mybatis.spring.nativex.mapper.Sample3Mapper;
import org.mybatis.spring.nativex.mapper.SampleMapper;
import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Test cases for {@link MyBatisMapperNativeConfigurationProcessor}.
 *
 * @author Kazuki Shimizu
 */
class MyBatisMapperNativeConfigurationProcessorTest {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Test
  @SuppressWarnings("java:S5961")
  void registerMapperInterfaceAndRelationships() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sampleMapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("mapperInterface", SampleMapper.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    // reflection hint
    {
      assertThat(hints.reflection().typeHints()).hasSize(13);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.class).withMemberCategories(MEMBER_CATEGORIES))
          .accepts(hints);
      assertThat(
          RuntimeHintsPredicates.reflection().onType(SampleMapper.Sample.class).withMemberCategories(MEMBER_CATEGORIES))
              .accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.SampleParam.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.Sample2.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.Sample2Param.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.SelectProviderClass1.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.SelectProviderClass2.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.InsertProviderClass1.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.InsertProviderClass2.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.UpdateProviderClass1.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.UpdateProviderClass2.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.DeleteProviderClass1.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
      assertThat(RuntimeHintsPredicates.reflection().onType(SampleMapper.DeleteProviderClass2.class)
          .withMemberCategories(MEMBER_CATEGORIES)).accepts(hints);
    }
    // proxy hint
    {
      assertThat(hints.proxies().jdkProxyHints()).hasSize(1);
      assertThat(RuntimeHintsPredicates.proxies().forInterfaces(SampleMapper.class)).accepts(hints);
    }
    // resource hint
    {
      assertThat(RuntimeHintsPredicates.resource().forResource("org/mybatis/spring/nativex/mapper/SampleMapper.xml"))
          .accepts(hints);
    }
  }

  @Test
  void clearConstructorArgumentsForMapperFactoryBean() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sampleMapper", BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
        .addConstructorArgValue(SampleMapper.class.getName()).addPropertyValue("mapperInterface", SampleMapper.class)
        .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
        .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    assertThat(hints.reflection().typeHints()).isNotEmpty();
    assertThat(beanFactory.getBeanDefinition("sampleMapper").getConstructorArgumentValues().isEmpty()).isTrue();
  }

  @Test
  void registerMultiMapperInterface() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("mapperInterface", Sample2Mapper.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    beanFactory.registerBeanDefinition("sample3Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("mapperInterface", Sample3Mapper.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    // reflection hint
    {
      assertThat(hints.reflection().typeHints()).hasSize(2);
      assertThat(
          RuntimeHintsPredicates.reflection().onType(Sample2Mapper.class).withMemberCategories(MEMBER_CATEGORIES))
              .accepts(hints);
      assertThat(
          RuntimeHintsPredicates.reflection().onType(Sample3Mapper.class).withMemberCategories(MEMBER_CATEGORIES))
              .accepts(hints);
    }
    // proxy hint
    {
      assertThat(hints.proxies().jdkProxyHints()).hasSize(2);
      assertThat(RuntimeHintsPredicates.proxies().forInterfaces(Sample2Mapper.class)).accepts(hints);
      assertThat(RuntimeHintsPredicates.proxies().forInterfaces(Sample3Mapper.class)).accepts(hints);
    }
    // resource hint
    {
      assertThat(RuntimeHintsPredicates.resource().forResource("org/mybatis/spring/nativex/mapper/Sample2Mapper.xml"))
          .accepts(hints);
      assertThat(RuntimeHintsPredicates.resource().forResource("org/mybatis/spring/nativex/mapper/Sample3Mapper.xml"))
          .accepts(hints);
    }
  }

  @Test
  void registerMapperInterfaceWithMapperFactoryBeanSubclass() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MyMapperFactoryBean.class)
            .addPropertyValue("mapperInterface", Sample2Mapper.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    // reflection hint
    {
      assertThat(hints.reflection().typeHints()).hasSize(1);
      assertThat(
          RuntimeHintsPredicates.reflection().onType(Sample2Mapper.class).withMemberCategories(MEMBER_CATEGORIES))
              .accepts(hints);
    }
    // proxy hint
    {
      assertThat(hints.proxies().jdkProxyHints()).hasSize(1);
      assertThat(RuntimeHintsPredicates.proxies().forInterfaces(Sample2Mapper.class)).accepts(hints);
    }
    // resource hint
    {
      assertThat(RuntimeHintsPredicates.resource().forResource("org/mybatis/spring/nativex/mapper/Sample2Mapper.xml"))
          .accepts(hints);
    }
  }

  @Test
  void skipRegisterWhenMapperInterfaceNotPresent() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    assertThat(hints.reflection().typeHints()).isEmpty();
    assertThat(hints.proxies().jdkProxyHints()).isEmpty();
    assertThat(hints.resources().resourcePatternHints()).isEmpty();
  }

  @Test
  void skipRegisterWhenMapperInterfaceIsNull() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class).addPropertyValue("mapperInterface", null)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    RuntimeHints hints = process(beanFactory);
    assertThat(hints.reflection().typeHints()).isEmpty();
    assertThat(hints.proxies().jdkProxyHints()).isEmpty();
    assertThat(hints.resources().resourcePatternHints()).isEmpty();
  }

  private RuntimeHints process(DefaultListableBeanFactory beanFactory) {
    RuntimeHints hints = new RuntimeHints();
    GenerationContext generationContext = mock(GenerationContext.class);
    when(generationContext.getRuntimeHints()).thenReturn(hints);
    BeanFactoryInitializationAotContribution contribution = new MyBatisMapperNativeConfigurationProcessor()
        .processAheadOfTime(beanFactory);
    if (contribution != null) {
      contribution.applyTo(generationContext, mock(BeanFactoryInitializationCode.class));
    }
    return hints;
  }

  private static class MyMapperFactoryBean<T> extends MapperFactoryBean<T> {

  }

}
