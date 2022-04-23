# 介绍

## 什么是 MyBatis Spring Native?

MyBatis Spring Native 帮助你在 [Spring Native](https://github.com/spring-projects-experimental/spring-native) 之上快速构建 MyBatis 应用。

## 支持的特性

### MyBatis 核心功能

- 使用与 SQL 相关的注解(`@Select`/`@Insert`/etc...)编写静态 SQL 语句和动态 SQL 语句（需要使用 ONGL 表达式）
- 使用类路径下的 XML 映射文件去载入 SQL 语句（例如：如果 mapper 接口的全限定名是 `com.example.SampleMapper`， 则使用 `com/example/SampleMapper.xml` 文件）
- 使用 SQL provider(`@SelectProvider`/`@InsertProvider`/etc...)
- 使用内置的二级缓存机制（内存中的二级缓存）

### MyBatis Spring

- 使用 `@MapperScan` 扫描用于映射的接口，而不是自动扫描

### MyBatis Spring Boot

- 自动配置 `SqlSessionFactory` 和 `SqlSessionTemplate` 
- 自动扫描带有 `@Mapper` 的 mapper 接口
- 使用配置文件（`application.properties`）来配置 Mybatis 的行为
- 从依赖注入容器中使用 MyBatis 的组件(`Interceptor`, `TypeHandler`, `LanguageDriver` 与 `DatabaseIdProvider`)
- 从依赖注入容器中使用配置组件 （实现了 `ConfigurationCustomizer` 或者 `SqlSessionFactoryBeanCustomizer`的类）

### MyBatis 扩展模块

- 支持与 [mybatis-thymeleaf](https://github.com/mybatis/thymeleaf-scripting) 组合使用
- 支持与 [mybatis-velocity](https://github.com/mybatis/velocity-scripting) 组合使用
- 支持与 [mybatis-freemarker](https://github.com/mybatis/freemarker-scripting) 组合使用
- 支持与 [mybatis-dynamic-sql](https://github.com/mybatis/mybatis-dynamic-sql) 组合使用

### MyBatis Spring Native

- 在编译时可以使用 `@MyBatisResourcesScan` 来扫描类型别名，类型处理器和 XML 映射文件（可以替代properties中相关的配置）
- 在编译时可以使用 `@MyBatisResourcesScan` 来扫描任意的类作为 reflection hint
- 在编译时可以使用 `@MyBatisResourcesScan` 来扫描任意的资源文件作为 resource hint
- 自动向 native hint(reflection hint) 注册参数类型，返回值类型和 sql provider 类型（但仅支持标准模式）

## 已知的局限性

- 如果你使用了 `MapperFactoryBean`的子类，可能不起作用。参见 [Fix early init of MapperFactoryBean by snicoll · Pull Request #32 · kazuki43zoo/mybatis-spring-native · GitHub](https://github.com/kazuki43zoo/mybatis-spring-native/pull/32)
- 不要向 native hint(reflection hint) 注册嵌套类型（保留参数和返回类型）
- 在 mapper 接口上使用 `@Transactional` 会导致初始化 bean 失败， 参见 [Fail bean initializing when specify @Transactional on mapper interface · Issue #2 · mybatis/spring-native · GitHub](https://github.com/mybatis/spring-native/issues/2)
- etc ...

# 组合支持的模块

为运行 Spring Native 提供配置。

| 名称                                 | 描述                                                                                                     |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------ |
| `mybatis-spring-native-core`       | 整合了 `mybatis` 和 `mybatis-spring`(`mybatis-spring-boot-starter`) 模块的基本特性                                |
| `mybatis-spring-native-extensions` | 整合了扩展模块 （使用 `mybatis-thymeleaf`, `mybatis-velocity`, `mybatis-freemarker` 与 `mybatis-dynamic-sql`) 的特性 |

# 使用

## 使用 @MyBatisResourcesScan

在 native-image 里面，动态扫描可能在运行时不起作用。
因此，我们支持在 Spring AOT 的特性下，使用 `@MyBatisResourcesScan` 来扫描类型别名，类型处理器和 XML 映射文件。

在启动时如果使用 `ConfigurationCustomizer` 和 `SqlSessionFactoryBeanCustomizer` 类，这些资源文件可以被用于 MyBatis 的组件。

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

**属性:**

| 属性                         | 描述                                                                    |
| -------------------------- | --------------------------------------------------------------------- |
| `typeAliasesPackages`      | Specify package names for scanning type aliases                       |
| `typeAliasesSupperType`    | Specify filter type(super class) for scanning type aliases            |
| `typeHandlerPackages`      | Specify package names for scanning type handlers                      |
| `mapperLocationPatterns`   | Specify location patterns for scanning mapper xml files               |
| `reflectionTypePackages`   | Specify package names for adding as reflection hint type              |
| `reflectionTypeSuperType`  | Specify filter type(super class) for scanning reflection type         |
| `typeAccesses`             | Specify access scopes for applying scanned classes to reflection hint |
| `resourceLocationPatterns` | Specify location patterns for adding as resource hint file            |

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

# Samples

Provides examples for running the MyBatis in spring-native.

| Name                                                  | Description                                                                                                                      |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------- |
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
