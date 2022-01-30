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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanFactoryNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeProxyEntry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeResourcesEntry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Finds and registers reflection hints for all scanned mappers in the beanFactory.
 *
 * @author Kazuki Shimizu
 * @author Josh Long
 */
public class MyBatisMapperNativeConfigurationProcessor implements BeanFactoryNativeConfigurationProcessor {

  private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

  private static final TypeAccess[] TYPE_ACCESSES = { PUBLIC_CONSTRUCTORS, PUBLIC_CLASSES, PUBLIC_FIELDS,
      PUBLIC_METHODS, DECLARED_CLASSES, DECLARED_CONSTRUCTORS, DECLARED_FIELDS, DECLARED_METHODS,
      QUERY_DECLARED_METHODS, QUERY_PUBLIC_METHODS, QUERY_DECLARED_CONSTRUCTORS, QUERY_PUBLIC_CONSTRUCTORS };

  /**
   * {@inheritDoc}
   */
  @Override
  public void process(ConfigurableListableBeanFactory beanFactory, NativeConfigurationRegistry registry) {
    if (ClassUtils.isPresent(MAPPER_FACTORY_BEAN, beanFactory.getBeanClassLoader())) {
      String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
      for (String beanName : beanNames) {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName.substring(1));
        PropertyValue mapperInterface = beanDefinition.getPropertyValues().getPropertyValue("mapperInterface");
        if (mapperInterface != null && mapperInterface.getValue() != null) {
          Class<?> mapperInterfaceType = (Class<?>) mapperInterface.getValue();
          registerReflectionTypeIfNecessary(mapperInterfaceType, registry);
          registry.proxy().add(NativeProxyEntry.ofInterfaces(mapperInterfaceType));
          registry.resources()
              .add(NativeResourcesEntry.of(mapperInterfaceType.getName().replace('.', '/').concat(".xml")));
          registerMapperRelationships(mapperInterfaceType, registry);
        }
      }
    }
  }

  private void registerMapperRelationships(Class<?> mapperInterfaceType, NativeConfigurationRegistry registry) {
    Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
    for (Method method : methods) {
      if (method.getDeclaringClass() != Object.class) {
        ReflectionUtils.makeAccessible(method);
        registerSqlProviderTypes(method, registry, SelectProvider.class, SelectProvider::value, SelectProvider::type);
        registerSqlProviderTypes(method, registry, InsertProvider.class, InsertProvider::value, InsertProvider::type);
        registerSqlProviderTypes(method, registry, UpdateProvider.class, UpdateProvider::value, UpdateProvider::type);
        registerSqlProviderTypes(method, registry, DeleteProvider.class, DeleteProvider::value, DeleteProvider::type);
        Class<?> returnType = MyBatisMapperTypeUtils.resolveReturnClass(mapperInterfaceType, method);
        registerReflectionTypeIfNecessary(returnType, registry);
        MyBatisMapperTypeUtils.resolveParameterClasses(mapperInterfaceType, method)
            .forEach(x -> registerReflectionTypeIfNecessary(x, registry));
      }
    }
  }

  @SafeVarargs
  private <T extends Annotation> void registerSqlProviderTypes(Method method, NativeConfigurationRegistry registry,
      Class<T> annotationType, Function<T, Class<?>>... providerTypeResolvers) {
    for (T annotation : method.getAnnotationsByType(annotationType)) {
      for (Function<T, Class<?>> providerTypeResolver : providerTypeResolvers) {
        registerReflectionTypeIfNecessary(providerTypeResolver.apply(annotation), registry);
      }
    }
  }

  private void registerReflectionTypeIfNecessary(Class<?> type, NativeConfigurationRegistry registry) {
    if (!type.isPrimitive() && !type.getName().startsWith("java")) {
      registry.reflection().forType(type).withAccess(TYPE_ACCESSES);
    }
  }

}
