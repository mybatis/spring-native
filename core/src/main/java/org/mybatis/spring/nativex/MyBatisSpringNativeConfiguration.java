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

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * Registers hints to make a MyBatis Spring component work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 */
// @formatter:off
@NativeHint(
    trigger = SqlSessionTemplate.class,
    resources = @ResourceHint(
        patterns = "org/mybatis/spring/config/.*.xsd"
    )
)
@TypeHint(
    types = SqlSessionFactoryBean.class,
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
public class MyBatisSpringNativeConfiguration implements NativeConfiguration {
}
