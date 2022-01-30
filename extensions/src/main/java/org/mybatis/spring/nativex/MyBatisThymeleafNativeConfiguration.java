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
package org.mybatis.spring.nativex;

import static org.springframework.nativex.hint.TypeAccess.DECLARED_CLASSES;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_FIELDS;
import static org.springframework.nativex.hint.TypeAccess.DECLARED_METHODS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_CLASSES;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_CONSTRUCTORS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_FIELDS;
import static org.springframework.nativex.hint.TypeAccess.PUBLIC_METHODS;

import org.mybatis.scripting.thymeleaf.SqlGeneratorConfig;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriverConfig;
import org.mybatis.scripting.thymeleaf.expression.Likes;
import org.mybatis.scripting.thymeleaf.support.TemplateFilePathProvider;
import org.mybatis.scripting.thymeleaf.support.spring.SpringNamedParameterBindVariableRender;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;
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
// @formatter:off
@NativeHint(
    trigger = ThymeleafLanguageDriver.class,
    resources = @ResourceHint(
        patterns = "mybatis-thymeleaf.properties"
    )
)
@TypeHint(
    types = {
        ThymeleafLanguageDriver.class,
        SqlGeneratorConfig.class,
        ThymeleafLanguageDriverConfig.class,
        TemplateFilePathProvider.class,
        SpringNamedParameterBindVariableRender.class,
        Likes.class,
        Uris.class,
        Calendars.class,
        Dates.class,
        Bools.class,
        Numbers.class,
        Objects.class,
        Strings.class,
        Arrays.class,
        Lists.class,
        Sets.class,
        Maps.class,
        Aggregates.class,
        Messages.class,
        Ids.class,
        ExecutionInfo.class
    },
    access = {
        PUBLIC_CONSTRUCTORS,
        PUBLIC_CLASSES,
        PUBLIC_FIELDS,
        PUBLIC_METHODS,
        DECLARED_CLASSES,
        DECLARED_CONSTRUCTORS,
        DECLARED_FIELDS,
        DECLARED_METHODS
    }
)
// @formatter:on
public class MyBatisThymeleafNativeConfiguration implements NativeConfiguration {
}
