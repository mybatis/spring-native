# Introduction

## What is MyBatis Spring Native?

The MyBatis Spring Native help you build quickly MyBatis applications on top of the [Spring Native](https://github.com/spring-projects-experimental/spring-native).

## Support features

### MyBatis core

* Write static SQLs and dynamic SQLs(with OGNL expression) in SQL annotations(`@Select`/`@Insert`/etc...)
* Detect rule based mapper xml file in classpath and load SQLs (e.g. If mapper interface FQCN is `com.example.SampleMapper`, detect `com/example/SampleMapper.xml` file)
* Use SQL providers(`@SelectProvider`/`@InsertProvider`/etc...)
* Use built-in 2nd cache feature(in-memory 2nd cache)

### MyBatis Spring

* Scan mapper interfaces using `@MapperScan` instead of automatically scan

### MyBatis Spring

* Configure the `SqlSessionFactory` and `SqlSessionTemplate` automatically
* Scan mapper interfaces annotated `@Mapper` automatically

### MyBatis Extension modules

* Support to integrate with [mybatis-thymeleaf](https://github.com/mybatis/thymeleaf-scripting)
* Support to integrate with [mybatis-velocity](https://github.com/mybatis/velocity-scripting)
* Support to integrate with [mybatis-freemarker](https://github.com/mybatis/freemarker-scripting)
* Support to integrate with [mybatis-dynamic-sql](https://github.com/mybatis/mybatis-dynamic-sql)

### MyBatis Spring Native

* Scan type aliases, type handlers and mapper xml file using `@MyBatisResourcesScan` at build time (Alternative as configuration properties)
* Scan any classes as reflection hint using `@MyBatisResourcesScan` at build time
* Scan any resources as resource hint using `@MyBatisResourcesScan` at build time
* Register parameter types, return types and sql provider types to native hint(reflection hint) automatically(support standard patterns only yet)

## Known Limitations

* May not work if you use a subclass of `MapperFactoryBean`, See https://github.com/kazuki43zoo/mybatis-spring-native/pull/32
* Does not register nested types(hold on parameter and return type) to native hint(reflection hint)
* Fail bean initializing when specify `@Transactional` on mapper interface, See https://github.com/kazuki43zoo/mybatis-spring-native/issues/29
* etc ...

# Modules

## Integrating support modules

Provides general configurations for running on spring-native.

| Name                               | Description                                                                                                                                     |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `mybatis-spring-native-core`       | Integrating module for `mybatis` and `mybatis-spring`(`mybatis-spring-boot-starter`) module basic features                                      |
| `mybatis-spring-native-extensions` | Integrating module for extension module(using `mybatis-thymeleaf`, `mybatis-velocity`, `mybatis-freemarker` and `mybatis-dynamic-sql`) features |

## Sample modules

Provides examples for running the MyBatis in spring-native.

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

# Usage

## Using @MyBatisResourcesScan

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

**Attributes:**

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

# Notices

## Using `@MapperScan`

If you use the `@MapperScan`, you should be specified either the `sqlSessionTemplateRef` or `sqlSessionFactoryRef` as follows:

```java
@MapperScan(basePackages = "com.example.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```

## Using 2nd cache

If you use the 2nd cache feature, you need to configure serialization hints.
And we recommend defining the [JEP-290 serial filter](https://docs.oracle.com/en/java/javase/11/core/serialization-filtering1.html).

> **IMPORTANT:**
>
> Please consider adding definition of JEP-290 serial filter when following warning log will output.
>
> ```
> 2022-01-16 13:18:21.045  WARN 21917 --- [           main] o.apache.ibatis.io.SerialFilterChecker   : As you are using functionality that deserializes object streams, it is recommended to define the JEP-290 serial filter. Please refer to https://docs.oracle.com/pls/topic/lookup?ctx=javase15&id=GUID-8296D8E8-2B93-4B9A-856E-0A65AF9B8C66
> ```

### How to configure serialization hints

Configure using `@SerializationHint`.

```java
@NativeHint(serializables = @SerializationHint(types = { ArrayList.class, City.class, String.class, Integer.class, Number.class })) // Adding @SerializationHint
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```
### How to define JEP-290 serial filter

Define `-Djdk.serialFilter`(system properties) on `buildArgs` of `native-maven-plugin` at `pom.xml`.

e.g.)

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
