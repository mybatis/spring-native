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
package org.mybatis.spring.nativex.sample.cache;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(MybatisSpringNativeSampleApplication.CacheRuntimeHints.class)
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {

  static class CacheRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
      hints.serialization().registerType(ArrayList.class);
      hints.serialization().registerType(City.class);
      hints.serialization().registerType(String.class);
      hints.serialization().registerType(Integer.class);
      hints.serialization().registerType(Number.class);
    }
  }

  private static final Logger log = LoggerFactory.getLogger("ApLog");

  public static void main(String[] args) {
    SpringApplication.run(MybatisSpringNativeSampleApplication.class, args);
  }

  @Bean
  ApplicationRunner runner(CityMapper mapper) {
    return args -> {
      City newCity = new City(null, "NYC", "NY", "USA");
      mapper.insert(newCity);
      log.info("New city: {}", newCity);
      mapper.findAll().forEach(x -> log.info("{}", x));
      mapper.findAll().forEach(x -> log.info("{}", x));
    };
  }

}
