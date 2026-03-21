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
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

/**
 * Registers hints to make a MyBatis Core components work in a Spring Native context.
 *
 * @author Kazuki Shimizu
 * @author Josh Long
 *
 * @see MyBatisMapperNativeConfigurationProcessor
 */
public class MyBatisCoreNativeConfiguration implements RuntimeHintsRegistrar {

  private static final MemberCategory[] MEMBER_CATEGORIES = { MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
      MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_PUBLIC_METHODS,
      MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS,
      MemberCategory.PUBLIC_CLASSES, MemberCategory.DECLARED_CLASSES };

  @Override
  public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
    if (!ClassUtils.isPresent("org.apache.ibatis.session.SqlSessionFactory", classLoader)) {
      return;
    }
    for (Class<?> type : new Class<?>[] { RawLanguageDriver.class, XMLLanguageDriver.class, RuntimeSupport.class,
        ProxyFactory.class, Slf4jImpl.class, Log.class, JakartaCommonsLoggingImpl.class, Log4j2Impl.class,
        Jdk14LoggingImpl.class, StdOutImpl.class, NoLoggingImpl.class, SqlSessionFactory.class, PerpetualCache.class,
        FifoCache.class, LruCache.class, SoftCache.class, WeakCache.class }) {
      hints.reflection().registerType(type, MEMBER_CATEGORIES);
    }
    hints.reflection().registerType(TypeReference.of("org.apache.ibatis.logging.log4j.Log4jImpl"), MEMBER_CATEGORIES);
    hints.resources().registerPattern("org/apache/ibatis/builder/xml/*.dtd");
    hints.resources().registerPattern("org/apache/ibatis/builder/xml/*.xsd");
  }

}
