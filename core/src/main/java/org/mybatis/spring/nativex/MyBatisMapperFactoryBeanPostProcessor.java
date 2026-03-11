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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.aot.BeanRegistrationAotContribution;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

/**
 * The {@code BeanRegistrationAotProcessor} for customizing a {@code MapperFactoryBean}.
 *
 * @author Stéphane Nicoll
 * @author Kazuki Shimizu
 */
class MyBatisMapperFactoryBeanPostProcessor implements BeanRegistrationAotProcessor {

  private static final Log LOG = LogFactory.getLog(MyBatisMapperFactoryBeanPostProcessor.class);

  private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

  @Override
  public BeanRegistrationAotContribution processAheadOfTime(RegisteredBean registeredBean) {
    Class<?> beanClass = registeredBean.getBeanClass();
    if (!ClassUtils.isPresent(MAPPER_FACTORY_BEAN, beanClass.getClassLoader())) {
      return null;
    }
    if (!MapperFactoryBean.class.isAssignableFrom(beanClass)) {
      return null;
    }
    RootBeanDefinition beanDefinition = registeredBean.getMergedBeanDefinition();
    if (beanDefinition.getResolvableType().hasUnresolvableGenerics()) {
      try {
        Class<?> mapperInterface = (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
        if (mapperInterface != null) {
          beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(beanClass, mapperInterface));
        }
      } catch (Exception e) {
        LOG.debug("Fail getting mapper interface type.", e);
      }
    }
    return null;
  }

}
