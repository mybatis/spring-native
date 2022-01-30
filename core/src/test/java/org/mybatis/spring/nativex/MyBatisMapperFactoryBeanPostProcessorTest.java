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

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.nativex.mapper.SampleMapper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Test cases for {@link MyBatisMapperFactoryBeanPostProcessor}.
 *
 * @author Kazuki Shimizu
 */
class MyBatisMapperFactoryBeanPostProcessorTest {

  @Test
  void resolveMapperInterfaceType() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
    Assertions.assertThat(beanDefinition.getTargetType()).isEqualTo(MapperFactoryBean.class);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()).hasSize(1);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()[0].toClass()).isEqualTo(SampleMapper.class);
  }

  @Test
  void resolveMapperInterfaceTypeWhenMapperFactoryBeanSubclassWithOneGeneric() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean2.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
    Assertions.assertThat(beanDefinition.getTargetType()).isEqualTo(MapperFactoryBean2.class);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()).hasSize(1);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()[0].toClass()).isEqualTo(SampleMapper.class);
  }

  @Test
  void resolveMapperInterfaceTypeWhenMapperFactoryBeanSubclassWithoutGeneric() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(SampleMapperFactoryBean.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
  }

  // TODO Now this case is limitation using MapperFactoryBean's subclass
  @Test
  void failResolveMapperInterfaceTypeWhenMapperFactoryBeanSubclassWithMultiGenerics() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean3.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> postProcess(beanDefinition)).withMessage(
        "Mismatched number of generics specified for private static class org.mybatis.spring.nativex.MyBatisMapperFactoryBeanPostProcessorTest$MapperFactoryBean3<T,Z>");
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenNotPresentMapperInterface() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenFailGetMapperInterface() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).addPropertyValue("mapperInterface", "invalid value")
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenNotPresentBeanClass() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder.rootBeanDefinition((Class<?>) null)
        .getBeanDefinition();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenBeanClassNotMapperBeanFactory() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder.rootBeanDefinition(String.class)
        .getBeanDefinition();
    postProcess(beanDefinition);
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  private void postProcess(RootBeanDefinition beanDefinition) {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    MyBatisMapperFactoryBeanPostProcessor processor = new MyBatisMapperFactoryBeanPostProcessor();
    processor.setBeanFactory(beanFactory);
    processor.postProcessBeanDefinition("testBean", beanDefinition);
  }

  private static class MapperFactoryBean2<T> extends MapperFactoryBean<T> {
  }

  @SuppressWarnings("unused")
  private static class MapperFactoryBean3<T, Z> extends MapperFactoryBean<T> {
  }

  private static class SampleMapperFactoryBean extends MapperFactoryBean<SampleMapper> {
  }

}
