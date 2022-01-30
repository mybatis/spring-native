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
package org.mybatis.spring.nativex.sample.dao;

import java.util.Collection;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

@Component
public class CityDao {

  private final SqlSession sqlSession;

  public CityDao(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  public void insert(City city) {
    sqlSession.insert("org.mybatis.spring.nativex.sample.dao.CityDao.insert", city);
  }

  public Collection<City> findAll() {
    return sqlSession.selectList("org.mybatis.spring.nativex.sample.dao.CityDao.findAll");
  }

}
