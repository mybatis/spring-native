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
package org.mybatis.spring.nativex.sample.dynamicsql;

import java.sql.JDBCType;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.CommonCountMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonDeleteMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonInsertMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonSelectMapper;
import org.mybatis.dynamic.sql.util.mybatis3.CommonUpdateMapper;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface CityMapper
    extends CommonSelectMapper, CommonCountMapper, CommonDeleteMapper, CommonInsertMapper<City>, CommonUpdateMapper {

  @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
  @Options(useGeneratedKeys = true, keyProperty = "row.id")
  int insert(InsertStatementProvider<City> insertStatement);

  @SelectProvider(type = SqlProviderAdapter.class, method = "select")
  List<City> selectMany(SelectStatementProvider selectStatement);

  default void insert(City city) {
    MyBatis3Utils.insert(this::insert, city, CityTable.INSTANCE,
        c -> c.map(CityTable.INSTANCE.id).toProperty("id").map(CityTable.INSTANCE.name).toProperty("name")
            .map(CityTable.INSTANCE.state).toProperty("state").map(CityTable.INSTANCE.country).toProperty("country"));
  }

  default Collection<City> findAll() {
    return MyBatis3Utils.selectList(this::selectMany, CityTable.INSTANCE.allColumn, CityTable.INSTANCE,
        SelectDSLCompleter.allRows());
  }

  class CityTable extends SqlTable {
    private static final CityTable INSTANCE = new CityTable();
    private final SqlColumn<Integer> id = column("id", JDBCType.INTEGER);
    private final SqlColumn<String> name = column("name", JDBCType.VARCHAR);
    private final SqlColumn<String> state = column("state", JDBCType.VARCHAR);
    private final SqlColumn<String> country = column("country", JDBCType.VARCHAR);
    private final BasicColumn[] allColumn = BasicColumn.columnList(id, name, state, country);

    private CityTable() {
      super("city");
    }
  }
}
