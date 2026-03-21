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

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;

/**
 * Registers hints to make a MyBatis Spring component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisSpringNativeConfiguration implements RuntimeHintsRegistrar {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    if (!ClassUtils.isPresent("org.mybatis.spring.SqlSessionTemplate", classLoader)) {
      return;
    }
    hints.reflection().registerType(SqlSessionFactoryBean.class, MEMBER_CATEGORIES);
    hints.resources().registerPattern("org/mybatis/spring/config/*.xsd");
  }

}
