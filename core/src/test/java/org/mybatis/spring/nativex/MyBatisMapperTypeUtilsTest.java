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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

class MyBatisMapperTypeUtilsTest {

  @Test
  void resolveReturnClassWithFinalValueClass() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper, ReflectionUtils.findMethod(mapper, "findString"));
    Assertions.assertThat(type).isEqualTo(String.class);
  }

  @Test
  void resolveReturnClassWithAbstractValueClass() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper, ReflectionUtils.findMethod(mapper, "findNumber"));
    Assertions.assertThat(type).isEqualTo(Number.class);
  }

  @Test
  void resolveReturnClassWithTopLayerInterface() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
        ReflectionUtils.findMethod(mapper, "findCharSequence"));
    Assertions.assertThat(type).isEqualTo(CharSequence.class);
  }

  @Test
  void resolveReturnClassWithPrimitive() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper, ReflectionUtils.findMethod(mapper, "findInt"));
    Assertions.assertThat(type).isEqualTo(int.class);
  }

  @Test
  void resolveReturnClassWithPrimitiveWrapper() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper, ReflectionUtils.findMethod(mapper, "findLong"));
    Assertions.assertThat(type).isEqualTo(Long.class);
  }

  @Test
  void resolveReturnClassWithPrimitiveVoid() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper, ReflectionUtils.findMethod(mapper, "insert"));
    Assertions.assertThat(type).isEqualTo(void.class);
  }

  @Test
  void resolveReturnClassWithClassVoid() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
        ReflectionUtils.findMethod(mapper, "insertWithClassVoid"));
    Assertions.assertThat(type).isEqualTo(Void.class);
  }

  @Test
  void resolveReturnClassWithSimpleBean() {
    Class<?> mapper = TestMapper1.class;
    Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
        ReflectionUtils.findMethod(mapper, "findSimpleBean"));
    Assertions.assertThat(type).isEqualTo(MyBean.class);
  }

  @Test
  void resolveReturnClassWithSimpleBeanInCollection() {
    Class<?> mapper = TestMapper1.class;
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanList"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanCollection"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanIterable"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanArray"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanMap"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanConcurrentMap"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
  }

  @Test
  void resolveReturnClassWithSimpleBeanInMap() {
    Class<?> mapper = TestMapper1.class;
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanMap"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanConcurrentMap"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
  }

  @Test
  void resolveReturnClassWithOptional() {
    Class<?> mapper = TestMapper1.class;
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findStringWithOptional"));
      Assertions.assertThat(type).isEqualTo(String.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleBeanWithOptional"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
  }

  @Test
  void resolveReturnClassWithWildcard() {
    Class<?> mapper = TestMapper1.class;
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleWildcardList"));
      Assertions.assertThat(type).isEqualTo(List.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          ReflectionUtils.findMethod(mapper, "findSimpleGenericsWildcardList"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
  }

  @Test
  void resolveReturnClassWithGenericsMapper() {
    Class<?> mapper = TestMapper2.class;
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          Arrays.stream(ReflectionUtils.getAllDeclaredMethods(mapper))
              .collect(Collectors.toMap(Method::getName, x -> x)).get("findOne"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
    {
      Class<?> type = MyBatisMapperTypeUtils.resolveReturnClass(mapper,
          Arrays.stream(ReflectionUtils.getAllDeclaredMethods(mapper))
              .collect(Collectors.toMap(Method::getName, x -> x)).get("findAll"));
      Assertions.assertThat(type).isEqualTo(MyBean.class);
    }
  }

  @Test
  void resolveParameterClassesSingleBean() {
    Class<?> mapper = TestMapper3.class;
    Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
        ReflectionUtils.findMethod(mapper, "insertWithBean", MyBean.class));
    Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
  }

  @Test
  void resolveParameterClassesMultiBean() {
    Class<?> mapper = TestMapper3.class;
    Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
        ReflectionUtils.findMethod(mapper, "insertWithMultiBean", MyBeanKey.class, MyBean.class));
    Assertions.assertThat(types).containsExactlyInAnyOrder(MyBeanKey.class, MyBean.class);
  }

  @Test
  void resolveParameterClassesWithBeanInMap() {
    Class<?> mapper = TestMapper3.class;
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithMap", Map.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithFixKeyMap", MyFixKeyMap.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithFixValueMap", MyFixValueMap.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(Integer.class);
    }
  }

  @Test
  void resolveParameterClassesWithBeanInCollection() {
    Class<?> mapper = TestMapper3.class;
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithBeanInList", List.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithBeanInCollection", Collection.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithBeanInIterable", Iterable.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithBeanInArray", MyBean[].class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
  }

  @Test
  void resolveParameterClassesWithWildcard() {
    Class<?> mapper = TestMapper3.class;
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithWildcardInList", List.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(Object.class);
    }
    {
      Set<Class<?>> types = MyBatisMapperTypeUtils.resolveParameterClasses(mapper,
          ReflectionUtils.findMethod(mapper, "insertWithGenericsWildcardInList", List.class));
      Assertions.assertThat(types).containsExactlyInAnyOrder(MyBean.class);
    }
  }

  @SuppressWarnings("unused")
  interface TestMapper1 {

    String findString();

    CharSequence findCharSequence();

    Number findNumber();

    Long findLong();

    int findInt();

    void insert();

    Void insertWithClassVoid();

    MyBean findSimpleBean();

    List<MyBean> findSimpleBeanList();

    Collection<MyBean> findSimpleBeanCollection();

    Iterable<MyBean> findSimpleBeanIterable();

    Map<String, MyBean> findSimpleBeanMap();

    ConcurrentMap<String, MyBean> findSimpleBeanConcurrentMap();

    MyBean[] findSimpleBeanArray();

    Optional<String> findStringWithOptional();

    Optional<MyBean> findSimpleBeanWithOptional();

    List<?> findSimpleWildcardList();

    <T extends MyBean> List<T> findSimpleGenericsWildcardList();

  }

  interface TestMapper2 extends BaseMapper<MyBean, MyBeanKey> {
  }

  @SuppressWarnings("unused")
  interface TestMapper3 {
    void insertWithBean(MyBean bean);

    void insertWithBeanInList(List<MyBean> beans);

    void insertWithBeanInCollection(Collection<MyBean> beans);

    void insertWithBeanInIterable(Iterable<MyBean> beans);

    void insertWithBeanInArray(MyBean[] beans);

    void insertWithMultiBean(MyBeanKey key, MyBean bean);

    void insertWithMap(Map<String, MyBean> beans);

    void insertWithFixKeyMap(MyFixKeyMap<MyBean> beans);

    void insertWithFixValueMap(MyFixValueMap<Integer> beans);

    void insertWithWildcardInList(List<?> beans);

    <T extends MyBean> void insertWithGenericsWildcardInList(List<T> beans);
  }

  @SuppressWarnings("unused")
  interface BaseMapper<T, K> {
    T findOne(K key);

    Collection<T> findAll();
  }

  static class MyBean {

  }

  static class MyBeanKey {

  }

  static class MyFixKeyMap<T> extends HashMap<String, T> {

  }

  static class MyFixValueMap<K> extends HashMap<K, MyBean> {

  }

}
