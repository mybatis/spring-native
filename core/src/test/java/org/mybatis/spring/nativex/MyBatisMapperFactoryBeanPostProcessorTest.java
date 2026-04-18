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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.nativex.mapper.SampleMapper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RegisteredBean;
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
    postProcess(MapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
    Assertions.assertThat(beanDefinition.getTargetType()).isEqualTo(MapperFactoryBean.class);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()).hasSize(1);
    Assertions.assertThat(beanDefinition.getResolvableType().getGenerics()[0].toClass()).isEqualTo(SampleMapper.class);
  }

  @Test
  void clearConstructorArgumentsWhenHasGenericConstructorArgument() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).addConstructorArgValue(SampleMapper.class.getName())
        .addPropertyValue("mapperInterface", SampleMapper.class).getBeanDefinition();
    Assertions.assertThat(beanDefinition.getConstructorArgumentValues().getArgumentCount()).isEqualTo(1);
    postProcess(MapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getConstructorArgumentValues().isEmpty()).isTrue();
  }

  @Test
  void clearConstructorArgumentsWhenHasIndexedConstructorArgument() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).addConstructorArgValue(SampleMapper.class.getName())
        .addPropertyValue("mapperInterface", SampleMapper.class).getBeanDefinition();
    beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, "sampleMapperClass");
    Assertions.assertThat(beanDefinition.getConstructorArgumentValues().getArgumentCount()).isEqualTo(2);
    postProcess(MapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getConstructorArgumentValues().isEmpty()).isTrue();
  }

  @Test
  void resolveMapperInterfaceTypeWhenMapperFactoryBeanSubclassWithOneGeneric() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean2.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(MapperFactoryBean2.class, beanDefinition);
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
    postProcess(SampleMapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isFalse();
  }

  // With BeanRegistrationAotProcessor, the processAheadOfTime method catches and ignores exceptions
  // during type resolution (unlike the old BeanDefinitionPostProcessor which propagated them).
  @Test
  void skipResolveMapperInterfaceTypeWhenMapperFactoryBeanSubclassWithMultiGenerics() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean3.class).addPropertyValue("mapperInterface", SampleMapper.class)
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(MapperFactoryBean3.class, beanDefinition);
    // exception is caught and ignored, target type remains unset
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenNotPresentMapperInterface() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(MapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenFailGetMapperInterface() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder
        .rootBeanDefinition(MapperFactoryBean.class).addPropertyValue("mapperInterface", "invalid value")
        .getBeanDefinition();
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    postProcess(MapperFactoryBean.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getResolvableType().hasUnresolvableGenerics()).isTrue();
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  @Test
  void skipResolveMapperInterfaceTypeWhenBeanClassNotMapperBeanFactory() {
    RootBeanDefinition beanDefinition = (RootBeanDefinition) BeanDefinitionBuilder.rootBeanDefinition(String.class)
        .getBeanDefinition();
    postProcess(String.class, beanDefinition);
    Assertions.assertThat(beanDefinition.getTargetType()).isNull();
  }

  private void postProcess(Class<?> beanClass, RootBeanDefinition beanDefinition) {
    RegisteredBean registeredBean = mock(RegisteredBean.class);
    doReturn(beanClass).when(registeredBean).getBeanClass();
    when(registeredBean.getMergedBeanDefinition()).thenReturn(beanDefinition);
    new MyBatisMapperFactoryBeanPostProcessor().processAheadOfTime(registeredBean);
  }

  private static class MapperFactoryBean2<T> extends MapperFactoryBean<T> {
  }

  @SuppressWarnings("unused")
  private static class MapperFactoryBean3<T, Z> extends MapperFactoryBean<T> {
  }

  private static class SampleMapperFactoryBean extends MapperFactoryBean<SampleMapper> {
  }

}
