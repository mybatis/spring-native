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
package org.mybatis.spring.nativex.component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

public class TypeHandlers {

  @SuppressWarnings("unused")
  BarTypeHandler anonymous = new BarTypeHandler() {
    @Override
    public String toString() {
      return super.toString();
    }
  };

  public static class InnerTypeHandler implements TypeHandler<String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) {

    }

    @Override
    public String getResult(ResultSet rs, String columnName) {
      return null;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) {
      return null;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) {
      return null;
    }
  }
}
