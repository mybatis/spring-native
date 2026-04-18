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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.function.Function;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Finds and registers reflection hints for all scanned mappers in the beanFactory.
 *
 * @author Kazuki Shimizu
 * @author Josh Long
 */
public class MyBatisMapperNativeConfigurationProcessor implements BeanFactoryInitializationAotProcessor {

  private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  /**
   * {@inheritDoc}
   */
  @Override
  public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
    if (!ClassUtils.isPresent(MAPPER_FACTORY_BEAN, beanFactory.getBeanClassLoader())) {
      return null;
    }
    String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
    if (beanNames.length == 0) {
      return null;
    }
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanFactory
          .getBeanDefinition(beanName.startsWith("&") ? beanName.substring(1) : beanName);
      ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
      if (!constructorArgumentValues.isEmpty()) {
        constructorArgumentValues.clear();
      }
    }
    return (generationContext, beanFactoryInitializationCode) -> {
      RuntimeHints hints = generationContext.getRuntimeHints();
      for (String beanName : beanNames) {
        BeanDefinition beanDefinition = beanFactory
            .getBeanDefinition(beanName.startsWith("&") ? beanName.substring(1) : beanName);
        PropertyValue mapperInterface = beanDefinition.getPropertyValues().getPropertyValue("mapperInterface");
        if (mapperInterface != null && mapperInterface.getValue() != null) {
          Class<?> mapperInterfaceType = (Class<?>) mapperInterface.getValue();
          if (mapperInterfaceType != null) {
            registerReflectionTypeIfNecessary(mapperInterfaceType, hints);
            hints.proxies().registerJdkProxy(mapperInterfaceType);
            hints.resources().registerPattern(mapperInterfaceType.getName().replace('.', '/').concat(".xml"));
            registerMapperRelationships(mapperInterfaceType, hints);
          }
        }
      }
    };
  }

  private void registerMapperRelationships(Class<?> mapperInterfaceType, RuntimeHints hints) {
    Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
    for (Method method : methods) {
      if (method.getDeclaringClass() != Object.class) {
        ReflectionUtils.makeAccessible(method);
        registerSqlProviderTypes(method, hints, SelectProvider.class, SelectProvider::value, SelectProvider::type);
        registerSqlProviderTypes(method, hints, InsertProvider.class, InsertProvider::value, InsertProvider::type);
        registerSqlProviderTypes(method, hints, UpdateProvider.class, UpdateProvider::value, UpdateProvider::type);
        registerSqlProviderTypes(method, hints, DeleteProvider.class, DeleteProvider::value, DeleteProvider::type);
        Class<?> returnType = MyBatisMapperTypeUtils.resolveReturnClass(mapperInterfaceType, method);
        registerReflectionTypeIfNecessary(returnType, hints);
        MyBatisMapperTypeUtils.resolveParameterClasses(mapperInterfaceType, method)
            .forEach(x -> registerReflectionTypeIfNecessary(x, hints));
      }
    }
  }

  @SafeVarargs
  private final <T extends Annotation> void registerSqlProviderTypes(Method method, RuntimeHints hints,
      Class<T> annotationType, Function<T, Class<?>>... providerTypeResolvers) {
    for (T annotation : method.getAnnotationsByType(annotationType)) {
      for (Function<T, Class<?>> providerTypeResolver : providerTypeResolvers) {
        registerReflectionTypeIfNecessary(providerTypeResolver.apply(annotation), hints);
      }
    }
  }

  private void registerReflectionTypeIfNecessary(Class<?> type, RuntimeHints hints) {
    if (!type.isPrimitive() && !type.getName().startsWith("java")) {
      hints.reflection().registerType(type, MEMBER_CATEGORIES);
    }
  }

}
