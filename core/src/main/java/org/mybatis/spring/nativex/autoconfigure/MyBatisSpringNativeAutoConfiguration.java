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
package org.mybatis.spring.nativex.autoconfigure;

import java.util.List;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.SqlSessionFactoryBeanCustomizer;
import org.mybatis.spring.nativex.MyBatisScannedResourcesHolder;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The configuration class that configure automatically for spring-native.
 *
 * @author Kazuki Shimizu
 */
@Configuration
@ConditionalOnClass({ org.apache.ibatis.session.Configuration.class, SqlSessionFactoryBeanCustomizer.class })
@AutoConfigureBefore(MybatisAutoConfiguration.class)
public class MyBatisSpringNativeAutoConfiguration {

  @ConditionalOnBean(MyBatisScannedResourcesHolder.class)
  @Bean
  ConfigurationCustomizer mybatisScannedResourcesHolderConfigurationCustomizer(
      List<MyBatisScannedResourcesHolder> holders) {
    return configuration -> holders.forEach(holder -> {
      holder.getTypeAliasesClasses().forEach(configuration.getTypeAliasRegistry()::registerAlias);
      holder.getTypeHandlerClasses().forEach(configuration.getTypeHandlerRegistry()::register);
    });
  }

  @ConditionalOnBean(MyBatisScannedResourcesHolder.class)
  @Bean
  SqlSessionFactoryBeanCustomizer mybatisScannedResourcesHolderSqlSessionFactoryBeanCustomizer(
      List<MyBatisScannedResourcesHolder> holders) {
    return factoryBean -> {
      Resource[] resources = holders.stream()
          .flatMap(holder -> holder.getMapperLocations().stream().map(ClassPathResource::new)).toArray(Resource[]::new);
      if (resources.length > 0) {
        factoryBean.setMapperLocations(resources);
      }
    };
  }

}
