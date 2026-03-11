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

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Finds and registers reflection and resource hints for all MyBatisScannedResourcesHolder in the {@code BeanFactory}.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisScannedResourcesNativeConfigurationProcessor implements BeanFactoryInitializationAotProcessor {

  private static final MemberCategory[] DEFAULT_MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  /**
   * {@inheritDoc}
   */
  @Override
  public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
    String[] beanNames = beanFactory.getBeanNamesForType(MyBatisScannedResourcesHolder.class);
    if (beanNames.length == 0) {
      return null;
    }
    return (generationContext, beanFactoryInitializationCode) -> {
      RuntimeHints hints = generationContext.getRuntimeHints();
      for (String beanName : beanNames) {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        @SuppressWarnings("unchecked")
        Set<String> resourceLocations = (Set<String>) Optional
            .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("resourceLocations"))
            .map(PropertyValue::getValue).orElse(Collections.emptySet());
        resourceLocations.forEach(hints.resources()::registerPattern);
        @SuppressWarnings("unchecked")
        Set<Class<?>> reflectionClasses = (Set<Class<?>>) Optional
            .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("reflectionClasses"))
            .map(PropertyValue::getValue).orElse(Collections.emptySet());
        MemberCategory[] memberCategories = (MemberCategory[]) Optional
            .ofNullable(beanDefinition.getPropertyValues().getPropertyValue("reflectionTypeAccesses"))
            .map(PropertyValue::getValue).orElse(DEFAULT_MEMBER_CATEGORIES);
        MemberCategory[] effectiveCategories = (memberCategories.length == 0) ? DEFAULT_MEMBER_CATEGORIES
            : memberCategories;
        reflectionClasses.forEach(x -> hints.reflection().registerType(x, effectiveCategories));
      }
    };
  }

}
