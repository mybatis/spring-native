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
package org.mybatis.spring.nativex.sample.thymeleaf;

import org.mybatis.scripting.thymeleaf.SqlGenerator;
import org.mybatis.scripting.thymeleaf.SqlGeneratorConfig;
import org.mybatis.scripting.thymeleaf.processor.BindVariableRender;
import org.mybatis.spring.nativex.MyBatisResourcesScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@MyBatisResourcesScan(reflectionTypePackages = "org.mybatis.spring.nativex.sample.thymeleaf", resourceLocationPatterns = "sqls/**/*.sql")
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {

  private static final Logger log = LoggerFactory.getLogger("ApLog");

  public static void main(String[] args) {
    SpringApplication.run(MybatisSpringNativeSampleApplication.class, args);
  }

  @Bean
  ApplicationRunner runner(NamedParameterJdbcOperations operations, SqlGenerator sqlGenerator) {
    return args -> {
      City newCity = new City(null, "NYC", "NY", "USA");
      KeyHolder keyHolder = new GeneratedKeyHolder();
      operations.update(sqlGenerator.generate("sqls/city/city-insert.sql", newCity),
          new BeanPropertySqlParameterSource(newCity), keyHolder);
      newCity.setId(keyHolder.getKeyAs(Integer.class));
      log.info("New city: {}", newCity);
      operations
          .query(sqlGenerator.generate("sqls/city/city-findAll.sql", null), new BeanPropertyRowMapper<>(City.class))
          .forEach(x -> log.info("{}", x));
    };
  }

  @Bean
  SqlGenerator sqlGenerator() {
    return new SqlGenerator(SqlGeneratorConfig.newInstanceWithCustomizer(
        c -> c.getDialect().setBindVariableRender(BindVariableRender.BuiltIn.SPRING_NAMED_PARAMETER.getType())));
  }

}
