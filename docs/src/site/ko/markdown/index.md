# 개요

## 마이바티스 스프링 네이티브란?

마이바티스 스프링 네이티브는 [Spring Native](https://github.com/spring-projects-experimental/spring-native)에 MyBatis 애플리케이션을 빠르게 만들 수 있도록 도와줍니다.
 
## 지원 기능

### 마이바티스 핵심

* SQL 주석(`@Select`/`@Insert`/기타 등등...)에 정적 SQLs과 동적 SQLs(OGNL 표현식으로) 작성
* 클래스경로와 로드  SQLs에 xml 파일 매퍼(mapper, 단계별 접근법) 기반으로 한 규칙 감지 (예시. 매퍼 인터페이스 정규화된 클래스 이름(FQCN)이 `com.example.SampleMapper`이면, `com/example/SampleMapper.xml` file를 찾아냅니다) 
* SQL 제공자 사용(`@SelectProvider`/`@InsertProvider`/기타 등등...)
* Use built-in 2nd cache feature(in-memory 2nd cache)
* 빌트인 2단계 캐시 기능

### 마이바티스 스프링

* 자동 스캔 대신 `@MapperScan`을 이용한 스캔 매퍼 인터페이스

### 마이바티스 스프링 부트

* `SqlSessionFactory` 와 `SqlSessionTemplate`의 자동 설정합니다
* `@Mapper`표기 된 매퍼 인터페이스 자동 스캔합니다
* 환경설정 속성(`application.properties`)을 이용한 마이바티스 작동 습성 커스토마이즈 맞춤 설정합니다
* DI 컨테이너에서 마이바티스 컴포넌트(`Interceptor`, `TypeHandler`, `LanguageDriver` 그리고 `DatabaseIdProvider`)를 찾아냅니다
* DI 컨테이너를 구성하는 커스토마이저 컴포넌트 (`ConfigurationCustomizer` 또는 `SqlSessionFactoryBeanCustomizer` 가 적용 된 클래스)를 찾아냅니다

### 마이바티스 확장 모듈

* [mybatis-thymeleaf](https://github.com/mybatis/thymeleaf-scripting) 통합 지원
* [mybatis-velocity](https://github.com/mybatis/velocity-scripting) 통합 지원
* [mybatis-freemarker](https://github.com/mybatis/freemarker-scripting) 통합 지원
* [mybatis-dynamic-sql](https://github.com/mybatis/mybatis-dynamic-sql) 통합 지원

### 마이바티스 스프링 네이티브

* `@MyBatisResourcesScan`를 이용한 타입 에일리어스, 타입 핸들러 그리고 xml file 매퍼 스캔
* 빌드 작동 시 `@MyBatisResourcesScan`를 이용하여 reflection hint로 어떤 클래스 든지 스캔
* 빌드 작동 시 `@MyBatisResourcesScan`를 이용하여 어떤 리소스 든지 스캔
* 파라미터 타입, 리턴 타입 그리고 네이티브 힌트(반사 힌트)에 대한 SQL 공급 타입 자동 등록 (표준 패턴만 지원)

## 알려진 제한사항

* `MapperFactoryBean`의 하위클래스 사용 시 작동하지 않을 수 있습니다. https://github.com/kazuki43zoo/mybatis-spring-native/pull/32 를 확인하세요
* 종속 타입(보류 파라미터와 리턴 타입)은 네이티브 힌트(반사 힌트)에 등록되지 않습니다
* 매퍼 인터페이스에 `@Transactional` 명기 시 빈  초기 설정 되지 않습니다.  https://github.com/mybatis/spring-native/issues/2 를 확인하세요
* 기타 등등...

# 통합 지원 모듈

스프링-네이티브 작동을 위한 일반 구성을  제공합니다

| Name                               | Description                                                                                                                                     |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `mybatis-spring-native-core`       | Integrating module for `mybatis` and `mybatis-spring`(`mybatis-spring-boot-starter`) module basic features                                      |
| `mybatis-spring-native-extensions` | Integrating module for extension module(using `mybatis-thymeleaf`, `mybatis-velocity`, `mybatis-freemarker` and `mybatis-dynamic-sql`) features |

# 사용법

## @MyBatisResourcesScan 이용하기

In native-image, dynamic scanning does not work at runtime.
Therefore, we support to scan type aliases, type handlers and mapper xml files at build time using Spring AOT feature.
These resources will apply to MyBatis components using `ConfigurationCustomizer` and `SqlSessionFactoryBeanCustomizer` at startup time.

```java
// ...
import org.mybatis.spring.nativex.MyBatisResourcesScan;
// ...
@MyBatisResourcesScan(typeAliasesPackages = "com.example.entity", mapperLocationPatterns = "mapper/**/*Mapper.xml")
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```

**속성:**

| Attribute | Description |
| --------- | ----------- |
| `typeAliasesPackages` | Specify package names for scanning type aliases |
| `typeAliasesSupperType` | Specify filter type(super class) for scanning type aliases |
| `typeHandlerPackages` | Specify package names for scanning type handlers |
| `mapperLocationPatterns` | Specify location patterns for scanning mapper xml files |
| `reflectionTypePackages` | Specify package names for adding as reflection hint type |
| `reflectionTypeSuperType` | Specify filter type(super class) for scanning reflection type |
| `typeAccesses` | Specify access scopes for applying scanned classes to reflection hint |
| `resourceLocationPatterns` | Specify location patterns for adding as resource hint file |

# 고지 사항

## `@MapperScan` 사용

`@MapperScan` 사용 한다면, `sqlSessionTemplateRef` 또는 `sqlSessionFactoryRef`이 다음과 같이 명기되어야 합니다:

```java
@MapperScan(basePackages = "com.example.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```

## 2nd cache 사용

만약 2nd cache 기능을 사용한다면, serialization hints 설정이 필요합니다.
따라서 [JEP-290 serial filter](https://docs.oracle.com/en/java/javase/11/core/serialization-filtering1.html)로 설정을 권합니다 .

> **중요:**
>
> 다음의 경고 로그가 출력 되는 경우, JEP-290 serial filter 정의를 추가하는 것을 고려하십시오.
>
> ```
> 2022-01-16 13:18:21.045  WARN 21917 --- [           main] o.apache.ibatis.io.SerialFilterChecker   : As you are using functionality that deserializes object streams, it is recommended to define the JEP-290 serial filter. Please refer to https://docs.oracle.com/pls/topic/lookup?ctx=javase15&id=GUID-8296D8E8-2B93-4B9A-856E-0A65AF9B8C66
> ```

### serialization hints 설정 방법

`@SerializationHint`를 사용하여 설정합니다.

```java
@NativeHint(serializables = @SerializationHint(types = { ArrayList.class, City.class, String.class, Integer.class, Number.class })) // Adding @SerializationHint
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```
### JEP-290 serial filter 정의 방법

Define `-Djdk.serialFilter`(system properties) on `buildArgs` of `native-maven-plugin` at `pom.xml`.
`pom.xml`에  `native-maven-plugin`의 `buildArgs` 상의 `-Djdk.serialFilter`(시스템 속성) 설정

예시)

```xml
<plugin>
  <groupId>org.graalvm.buildtools</groupId>
  <artifactId>native-maven-plugin</artifactId>
  <version>${native-buildtools.version}</version>
  <extensions>true</extensions>
  <configuration>
    <buildArgs>
      <arg>-Djdk.serialFilter=org.mybatis.spring.nativex.sample.cache.*;java.util.*;java.lang.*;!*</arg> <!-- Adding definition -->
    </buildArgs>
  </configuration>
  <!-- ... -->
</plugin>
```

# 샘플

스프링-네이티브에 마이바티스 실행을 위한 예제를 제공합니다.

| Name                                                  | Description                                                                                                                      |
|-------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `mybatis-spring-native-sample-simple`                 | The very simple sample application using annotation driven mapper (`@Select`/`@Insert`/etc...)                                   |
| `mybatis-spring-native-sample-xml`                    | The very simple sample application using xml file driven mapper                                                                  |
| `mybatis-spring-native-sample-sqlprovider`            | The very simple sample application using SQL provider driven mapper (`@SelectProvider`/`@InsertProvider`/etc...)                 |
| `mybatis-spring-native-sample-scan`                   | The sample application using `@MapperScan` and `@MyBatisResourcesScan` annotation                                                |
| `mybatis-spring-native-sample-dao`                    | The sample application with DAO pattern (without mapper interface)                                                               |
| `mybatis-spring-native-sample-thymeleaf`              | The sample application using `mybatis-thymeleaf`                                                                                 |
| `mybatis-spring-native-sample-thymeleaf-sqlgenerator` | The sample application using `SqlGenerator` provided by `mybatis-thymeleaf` without `mybatis` and `mybatis-spring` module        |
| `mybatis-spring-native-sample-velocity`               | The sample application using `mybatis-velocity`                                                                                  |
| `mybatis-spring-native-sample-freemarker`             | The sample application using `mybatis-freemarker`                                                                                |
| `mybatis-spring-native-sample-cache`                  | The sample application with built-in 2nd cache feature                                                                           |
| `mybatis-spring-native-sample-configuration`          | The sample application with customizing MyBatis's configuration using configuration properties feature(`application.properties`) |
| `mybatis-spring-native-sample-dynamic-sql`            | The sample application using `mybatis-dynamic-sql`                                                                               |

# 번역

마이바티스-스프링-네이티브에 대하여 다음 언어로 확인 가능합니다:

<ul class="i18n">
  <li class="en"><a href="./../index.html">English</a></li>
  <li class="zh"><a href="./../zh/index.html">简体中文</a></li>
  <li class="ko"><a href="./../ko/index.html">한국어</a></li>
</ul>