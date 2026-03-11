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

import org.mybatis.dynamic.sql.delete.render.DefaultDeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.DefaultGeneralInsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.DefaultInsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.DefaultMultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.DefaultSelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.DefaultUpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.springbatch.SpringBatchProviderAdapter;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/**
 * Registers hints to make a MyBatis Dynamic SQL component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisDynamicSqlNativeConfiguration implements RuntimeHintsRegistrar {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    if (!ClassUtils.isPresent("org.mybatis.dynamic.sql.util.SqlProviderAdapter", classLoader)) {
      return;
    }
    for (Class<?> type : new Class<?>[] { SqlProviderAdapter.class, SpringBatchProviderAdapter.class,
        DefaultDeleteStatementProvider.class, DefaultGeneralInsertStatementProvider.class,
        DefaultInsertStatementProvider.class, DefaultMultiRowInsertStatementProvider.class,
        DefaultSelectStatementProvider.class, DefaultUpdateStatementProvider.class }) {
      hints.reflection().registerType(type, MEMBER_CATEGORIES);
    }
  }

}
