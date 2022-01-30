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

import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.nativex.hint.InitializationHint;
import org.springframework.nativex.hint.InitializationTime;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;

/**
 * Registers hints to make a MyBatis Core components work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 * @author Josh Long
 * @see MyBatisMapperNativeConfigurationProcessor
 */
// @formatter:off
@NativeHint(
    trigger = SqlSessionFactory.class,
    initialization = @InitializationHint(
        initTime = InitializationTime.BUILD,
        types = org.apache.ibatis.type.JdbcType.class
    ),
    options = "--initialize-at-build-time=org.apache.ibatis.type.JdbcType",
    resources = @ResourceHint(
        patterns = {
            "org/apache/ibatis/builder/xml/.*.dtd",
            "org/apache/ibatis/builder/xml/.*.xsd"
        }
    )
)
@TypeHint(
    types = {
        RawLanguageDriver.class,
        XMLLanguageDriver.class,
        RuntimeSupport.class,
        ProxyFactory.class,
        Slf4jImpl.class,
        Log.class,
        JakartaCommonsLoggingImpl.class,
        Log4j2Impl.class,
        Jdk14LoggingImpl.class,
        StdOutImpl.class,
        NoLoggingImpl.class,
        SqlSessionFactory.class,
        PerpetualCache.class,
        FifoCache.class,
        LruCache.class,
        SoftCache.class,
        WeakCache.class
    },
    typeNames = "org.apache.ibatis.logging.log4j.Log4jImpl",
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
public class MyBatisCoreNativeConfiguration implements NativeConfiguration {
}
