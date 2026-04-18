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

import org.mybatis.scripting.thymeleaf.SqlGeneratorConfig;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriverConfig;
import org.mybatis.scripting.thymeleaf.expression.Likes;
import org.mybatis.scripting.thymeleaf.support.TemplateFilePathProvider;
import org.mybatis.scripting.thymeleaf.support.spring.SpringNamedParameterBindVariableRender;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.util.ClassUtils;
import org.thymeleaf.expression.Aggregates;
import org.thymeleaf.expression.Arrays;
import org.thymeleaf.expression.Bools;
import org.thymeleaf.expression.Calendars;
import org.thymeleaf.expression.Dates;
import org.thymeleaf.expression.ExecutionInfo;
import org.thymeleaf.expression.Ids;
import org.thymeleaf.expression.Lists;
import org.thymeleaf.expression.Maps;
import org.thymeleaf.expression.Messages;
import org.thymeleaf.expression.Numbers;
import org.thymeleaf.expression.Objects;
import org.thymeleaf.expression.Sets;
import org.thymeleaf.expression.Strings;
import org.thymeleaf.expression.Uris;

/**
 * Registers hints to make a MyBatis Thymeleaf component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
public class MyBatisThymeleafNativeConfiguration implements RuntimeHintsRegistrar {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    if (!ClassUtils.isPresent("org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver", classLoader)) {
      return;
    }
    for (Class<?> type : new Class<?>[] { ThymeleafLanguageDriver.class, SqlGeneratorConfig.class,
        ThymeleafLanguageDriverConfig.class, TemplateFilePathProvider.class,
        SpringNamedParameterBindVariableRender.class, Likes.class, Uris.class, Calendars.class, Dates.class,
        Bools.class, Numbers.class, Objects.class, Strings.class, Arrays.class, Lists.class, Sets.class, Maps.class,
        Aggregates.class, Messages.class, Ids.class, ExecutionInfo.class }) {
      hints.reflection().registerType(type, MEMBER_CATEGORIES);
    }
    hints.resources().registerPattern("mybatis-thymeleaf.properties");
  }

}
