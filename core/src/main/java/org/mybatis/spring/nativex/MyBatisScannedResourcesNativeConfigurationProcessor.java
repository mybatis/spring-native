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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanFactoryNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeResourcesEntry;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.nativex.hint.TypeAccess;

/**
 * Finds and registers reflection and resource hints for all MyBatisScannedResourcesHolder in the {@code BeanFactory}.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisScannedResourcesNativeConfigurationProcessor implements BeanFactoryNativeConfigurationProcessor {

  private static final TypeAccess[] DEFAULT_TYPE_ACCESSES = { PUBLIC_CONSTRUCTORS, PUBLIC_CLASSES, PUBLIC_FIELDS,
      PUBLIC_METHODS, DECLARED_CLASSES, DECLARED_CONSTRUCTORS, DECLARED_FIELDS, DECLARED_METHODS,
      QUERY_DECLARED_METHODS, QUERY_PUBLIC_METHODS, QUERY_DECLARED_CONSTRUCTORS, QUERY_PUBLIC_CONSTRUCTORS };

  /**
   * {@inheritDoc}
   */
  @Override
  public void process(ConfigurableListableBeanFactory beanFactory, NativeConfigurationRegistry registry) {
    String[] beanNames = beanFactory.getBeanNamesForType(MyBatisScannedResourcesHolder.class);
    for (String beanName : beanNames) {
      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
      @SuppressWarnings("unchecked")
      Set<String> resourceLocations = (Set<String>) Optional
          .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("resourceLocations"))
          .map(PropertyValue::getValue).orElse(Collections.emptySet());
      resourceLocations.forEach(x -> registry.resources().add(NativeResourcesEntry.of(x)));
      @SuppressWarnings("unchecked")
      Set<Class<?>> reflectionClasses = (Set<Class<?>>) Optional
          .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("reflectionClasses"))
          .map(PropertyValue::getValue).orElse(Collections.emptySet());
      TypeAccess[] reflectionTypeAccesses = (TypeAccess[]) Optional
          .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("reflectionTypeAccesses"))
          .map(PropertyValue::getValue).orElse(DEFAULT_TYPE_ACCESSES);
      reflectionClasses.forEach(x -> registry.reflection().forType(x)
          .withAccess(reflectionTypeAccesses.length == 0 ? DEFAULT_TYPE_ACCESSES : reflectionTypeAccesses).build());
    }
  }

}
