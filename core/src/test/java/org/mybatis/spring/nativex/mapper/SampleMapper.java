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
package org.mybatis.spring.nativex.mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;

public interface SampleMapper {

  Sample find(SampleParam param);

  default Collection<Sample2> find(Collection<Sample2Param> params) {
    return Collections.emptyList();
  }

  @SelectProvider(SelectProviderClass1.class)
  String select1();

  @SelectProvider(type = SelectProviderClass2.class)
  BigDecimal select2();

  @InsertProvider(InsertProviderClass1.class)
  void insert1();

  @InsertProvider(type = InsertProviderClass2.class)
  boolean insert2();

  @InsertProvider(UpdateProviderClass1.class)
  Long update1();

  @InsertProvider(type = UpdateProviderClass2.class)
  Date update2();

  @InsertProvider(DeleteProviderClass1.class)
  Short delete1();

  @InsertProvider(type = DeleteProviderClass2.class)
  Object delete2();

  int count();

  class SampleParam {

  }

  class Sample {

  }

  class Sample2Param {

  }

  class Sample2 {

  }

  class SelectProviderClass1 {

  }

  class SelectProviderClass2 {

  }

  class InsertProviderClass1 {

  }

  class InsertProviderClass2 {

  }

  class UpdateProviderClass1 {

  }

  class UpdateProviderClass2 {

  }

  class DeleteProviderClass1 {

  }

  class DeleteProviderClass2 {

  }
}
