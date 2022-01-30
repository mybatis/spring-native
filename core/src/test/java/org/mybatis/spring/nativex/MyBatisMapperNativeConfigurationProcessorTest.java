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
import static org.springframework.nativex.hint.TypeAccess.QUERY_DECLARED_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.QUERY_DECLARED_METHODS;
import static org.springframework.nativex.hint.TypeAccess.QUERY_PUBLIC_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.QUERY_PUBLIC_METHODS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.nativex.mapper.Sample2Mapper;
import org.mybatis.spring.nativex.mapper.Sample3Mapper;
import org.mybatis.spring.nativex.mapper.SampleMapper;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.DefaultNativeReflectionEntry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeProxyEntry;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.nativex.domain.proxies.JdkProxyDescriptor;
import org.springframework.nativex.domain.proxies.ProxiesDescriptor;
import org.springframework.nativex.hint.TypeAccess;

/**
 * Test cases for {@link MyBatisMapperNativeConfigurationProcessor}.
 *
 * @author Kazuki Shimizu
 */
class MyBatisMapperNativeConfigurationProcessorTest {

  private static final TypeAccess[] TYPE_ACCESSES = { PUBLIC_CONSTRUCTORS, PUBLIC_CLASSES, PUBLIC_FIELDS,
      PUBLIC_METHODS, DECLARED_CLASSES, DECLARED_CONSTRUCTORS, DECLARED_FIELDS, DECLARED_METHODS,
      QUERY_DECLARED_METHODS, QUERY_PUBLIC_METHODS, QUERY_DECLARED_CONSTRUCTORS, QUERY_PUBLIC_CONSTRUCTORS };

  @Test
  void registerMapperInterfaceAndRelationships() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sampleMapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("mapperInterface", SampleMapper.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    NativeConfigurationRegistry registry = process(beanFactory);
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).hasSize(13);
      // mapper interface
      Assertions.assertThat(entries.get(SampleMapper.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      // mapper method argument and return type
      Assertions.assertThat(entries.get(SampleMapper.Sample.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.SampleParam.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.Sample2.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.Sample2Param.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      // sql provider
      Assertions.assertThat(entries.get(SampleMapper.SelectProviderClass1.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.SelectProviderClass2.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.InsertProviderClass1.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.InsertProviderClass2.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.UpdateProviderClass1.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.UpdateProviderClass2.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.DeleteProviderClass1.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(SampleMapper.DeleteProviderClass2.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
    }
    // proxy hint
    {
      List<NativeProxyEntry> entries = new ArrayList<>(registry.proxy().getEntries());
      Assertions.assertThat(entries).hasSize(1);
      ProxiesDescriptor proxiesDescriptor = new ProxiesDescriptor();
      entries.forEach(x -> x.contribute(proxiesDescriptor));
      List<JdkProxyDescriptor> jdkProxyDescriptors = new ArrayList<>(proxiesDescriptor.getProxyDescriptors());
      Assertions.assertThat(jdkProxyDescriptors).hasSize(1);
      Assertions.assertThat(jdkProxyDescriptors.get(0)).satisfies(x -> {
        Assertions.assertThat(x.isClassProxy()).isFalse();
        Assertions.assertThat(x.getTypes()).containsExactlyInAnyOrder("org.mybatis.spring.nativex.mapper.SampleMapper");
      });
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).containsExactlyInAnyOrder("org/mybatis/spring/nativex/mapper/SampleMapper.xml");
    }
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
    NativeConfigurationRegistry registry = process(beanFactory);
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).hasSize(2);
      // mapper interface
      Assertions.assertThat(entries.get(Sample2Mapper.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
      Assertions.assertThat(entries.get(Sample3Mapper.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
    }
    // proxy hint
    {
      List<NativeProxyEntry> entries = new ArrayList<>(registry.proxy().getEntries());
      Assertions.assertThat(entries).hasSize(2);
      ProxiesDescriptor proxiesDescriptor = new ProxiesDescriptor();
      entries.forEach(x -> x.contribute(proxiesDescriptor));
      List<JdkProxyDescriptor> jdkProxyDescriptors = new ArrayList<>(proxiesDescriptor.getProxyDescriptors());
      Assertions.assertThat(jdkProxyDescriptors).hasSize(2);
      Assertions.assertThat(jdkProxyDescriptors.get(0)).satisfies(x -> {
        Assertions.assertThat(x.isClassProxy()).isFalse();
        Assertions.assertThat(x.getTypes())
            .containsExactlyInAnyOrder("org.mybatis.spring.nativex.mapper.Sample2Mapper");
      });
      Assertions.assertThat(jdkProxyDescriptors.get(1)).satisfies(x -> {
        Assertions.assertThat(x.isClassProxy()).isFalse();
        Assertions.assertThat(x.getTypes())
            .containsExactlyInAnyOrder("org.mybatis.spring.nativex.mapper.Sample3Mapper");
      });
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).containsExactlyInAnyOrder("org/mybatis/spring/nativex/mapper/Sample2Mapper.xml",
          "org/mybatis/spring/nativex/mapper/Sample3Mapper.xml");
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
    NativeConfigurationRegistry registry = process(beanFactory);
    // reflection hint
    {
      Map<Class<?>, DefaultNativeReflectionEntry> entries = registry.reflection().reflectionEntries()
          .collect(Collectors.toMap(DefaultNativeReflectionEntry::getType, x -> x));
      Assertions.assertThat(entries).hasSize(1);
      // mapper interface
      Assertions.assertThat(entries.get(Sample2Mapper.class))
          .satisfies(x -> Assertions.assertThat(x.getAccess().toArray()).isEqualTo(TYPE_ACCESSES));
    }
    // proxy hint
    {
      List<NativeProxyEntry> entries = new ArrayList<>(registry.proxy().getEntries());
      Assertions.assertThat(entries).hasSize(1);
      ProxiesDescriptor proxiesDescriptor = new ProxiesDescriptor();
      entries.forEach(x -> x.contribute(proxiesDescriptor));
      List<JdkProxyDescriptor> jdkProxyDescriptors = new ArrayList<>(proxiesDescriptor.getProxyDescriptors());
      Assertions.assertThat(jdkProxyDescriptors).hasSize(1);
      Assertions.assertThat(jdkProxyDescriptors.get(0)).satisfies(x -> {
        Assertions.assertThat(x.isClassProxy()).isFalse();
        Assertions.assertThat(x.getTypes())
            .containsExactlyInAnyOrder("org.mybatis.spring.nativex.mapper.Sample2Mapper");
      });
    }
    // resource hint
    {
      Set<String> resources = registry.resources().toResourcesDescriptor().getPatterns();
      Assertions.assertThat(resources).containsExactlyInAnyOrder("org/mybatis/spring/nativex/mapper/Sample2Mapper.xml");
    }
  }

  @Test
  void skipRegisterWhenMapperInterfaceNotPresent() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    NativeConfigurationRegistry registry = process(beanFactory);
    // reflection hint
    {
      Assertions.assertThat(registry.reflection().reflectionEntries()).isEmpty();
    }
    // proxy hint
    {
      Assertions.assertThat(registry.proxy().getEntries()).isEmpty();
    }
    // resource hint
    {
      Assertions.assertThat(registry.resources().toResourcesDescriptor().getPatterns()).isEmpty();
    }
  }

  @Test
  void skipRegisterWhenMapperInterfaceIsNull() {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerBeanDefinition("sample2Mapper",
        BeanDefinitionBuilder.rootBeanDefinition(MapperFactoryBean.class).addPropertyValue("mapperInterface", null)
            .addPropertyValue("sqlSessionTemplate", new RuntimeBeanNameReference("sqlSessionTemplate"))
            .getBeanDefinition());
    NativeConfigurationRegistry registry = process(beanFactory);
    // reflection hint
    {
      Assertions.assertThat(registry.reflection().reflectionEntries()).isEmpty();
    }
    // proxy hint
    {
      Assertions.assertThat(registry.proxy().getEntries()).isEmpty();
    }
    // resource hint
    {
      Assertions.assertThat(registry.resources().toResourcesDescriptor().getPatterns()).isEmpty();
    }
  }

  private NativeConfigurationRegistry process(DefaultListableBeanFactory beanFactory) {
    NativeConfigurationRegistry registry = new NativeConfigurationRegistry();
    new MyBatisMapperNativeConfigurationProcessor().process(beanFactory, registry);
    return registry;
  }

  private static class MyMapperFactoryBean<T> extends MapperFactoryBean<T> {

  }

}
