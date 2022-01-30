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
package org.mybatis.spring.nativex.sample.configuration;

import java.util.Collection;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CityMapper {

  @Insert("INSERT INTO city (city_name, state_code, country_code) VALUES(#{cityName}, #{stateCode}, #{countryCode})")
  @Options(useGeneratedKeys = true, keyProperty = "cityId")
  void insert(City city);

  @Select("SELECT city_id, city_name, state_code, country_code FROM city ORDER BY city_id")
  Collection<City> findAll();

}
