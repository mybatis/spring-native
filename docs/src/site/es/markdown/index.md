# Introducción

## ¿Qué es MyBatis Spring Native?

MyBatis Spring Native te ayuda a construir rápidamente aplicaciones MyBatis sobre [Spring Native](https://github.com/spring-projects-experimental/spring-native).

## Funcionalidades soportadas

### MyBatis core

* Escribir SQL estático y SQL dinámico (con expresiones OGNL) en anotaciones SQL (`@Select`/`@Insert`/etc...)
* Detectar el fichero XML del mapper según la regla de classpath y cargar el SQL (p. ej. si el FQCN de la interfaz mapper es `com.example.SampleMapper`, se detecta el fichero `com/example/SampleMapper.xml`)
* Usar proveedores SQL (`@SelectProvider`/`@InsertProvider`/etc...)
* Usar la caché de segundo nivel integrada (caché de segundo nivel en memoria)

### MyBatis Spring

* Escanear interfaces mapper con `@MapperScan` en lugar del escaneo automático

### MyBatis Spring Boot

* Configurar `SqlSessionFactory` y `SqlSessionTemplate` automáticamente
* Escanear automáticamente las interfaces mapper anotadas con `@Mapper`
* Personalizar el comportamiento de MyBatis con propiedades de configuración (`application.properties`)
* Detectar componentes MyBatis (`Interceptor`, `TypeHandler`, `LanguageDriver` y `DatabaseIdProvider`) desde el contenedor de DI
* Detectar componentes personalizadores (clases que implementan `ConfigurationCustomizer` o `SqlSessionFactoryBeanCustomizer`) desde el contenedor de DI

### Módulos de extensión de MyBatis

* Integración con [mybatis-thymeleaf](https://github.com/mybatis/thymeleaf-scripting)
* Integración con [mybatis-velocity](https://github.com/mybatis/velocity-scripting)
* Integración con [mybatis-freemarker](https://github.com/mybatis/freemarker-scripting)
* Integración con [mybatis-dynamic-sql](https://github.com/mybatis/mybatis-dynamic-sql)

### MyBatis Spring Native

* Escanear alias de tipo, type handlers y ficheros XML de mapper con `@MyBatisResourcesScan` en tiempo de compilación (alternativa a las propiedades de configuración)
* Escanear cualquier clase como pista de reflexión con `@MyBatisResourcesScan` en tiempo de compilación
* Escanear cualquier recurso como pista de recurso con `@MyBatisResourcesScan` en tiempo de compilación
* Registrar automáticamente tipos de parámetro, tipos de retorno y tipos de SQL provider en las pistas nativas (pistas de reflexión) (de momento solo patrones estándar)

## Limitaciones conocidas

* Puede no funcionar si usas una subclase de `MapperFactoryBean`, véase https://github.com/kazuki43zoo/mybatis-spring-native/pull/32
* No registra tipos anidados (contenidos en el tipo de parámetro y de retorno) en las pistas nativas (pistas de reflexión)
* Falla la inicialización del bean si se especifica `@Transactional` en la interfaz mapper, véase https://github.com/mybatis/spring-native/issues/2
* etc ...

# Módulos de integración

Proporciona configuraciones generales para ejecutarse sobre spring-native.

| Nombre                             | Descripción                                                                                                                                     |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| `mybatis-spring-native-core`       | Módulo de integración de las funcionalidades básicas de `mybatis` y `mybatis-spring` (`mybatis-spring-boot-starter`)                            |
| `mybatis-spring-native-extensions` | Módulo de integración de las extensiones (`mybatis-thymeleaf`, `mybatis-velocity`, `mybatis-freemarker` y `mybatis-dynamic-sql`)                |

# Uso

## Usar @MyBatisResourcesScan

En native-image, el escaneo dinámico no funciona en tiempo de ejecución.
Por eso se puede escanear alias de tipo, type handlers y ficheros XML de mapper en tiempo de compilación con la funcionalidad AOT de Spring.
Estos recursos se aplican a los componentes de MyBatis con `ConfigurationCustomizer` y `SqlSessionFactoryBeanCustomizer` al arrancar.

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

**Atributos:**

| Atributo | Descripción |
| --------- | ----------- |
| `typeAliasesPackages` | Nombres de paquete para escanear alias de tipo |
| `typeAliasesSupperType` | Tipo filtro (superclase) para escanear alias de tipo |
| `typeHandlerPackages` | Nombres de paquete para escanear type handlers |
| `mapperLocationPatterns` | Patrones de ubicación para escanear ficheros XML de mapper |
| `reflectionTypePackages` | Nombres de paquete que se añaden como tipos de pista de reflexión |
| `reflectionTypeSuperType` | Tipo filtro (superclase) para escanear tipos de reflexión |
| `typeAccesses` | Ámbitos de acceso al aplicar las clases escaneadas a la pista de reflexión |
| `resourceLocationPatterns` | Patrones de ubicación que se añaden como ficheros de pista de recurso |

# Avisos

## Usar `@MapperScan`

Si usas `@MapperScan`, debes especificar `sqlSessionTemplateRef` o `sqlSessionFactoryRef` de la siguiente forma:

```java
@MapperScan(basePackages = "com.example.mapper", sqlSessionTemplateRef = "sqlSessionTemplate")
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```

## Usar la caché de segundo nivel

Si usas la caché de segundo nivel, hay que configurar pistas de serialización.
Además recomendamos definir el [filtro de serialización JEP-290](https://docs.oracle.com/en/java/javase/11/core/serialization-filtering1.html).

> **IMPORTANTE:**
>
> Considera añadir la definición del filtro de serialización JEP-290 cuando aparezca el siguiente aviso.
>
> ```
> 2022-01-16 13:18:21.045  WARN 21917 --- [           main] o.apache.ibatis.io.SerialFilterChecker   : As you are using functionality that deserializes object streams, it is recommended to define the JEP-290 serial filter. Please refer to https://docs.oracle.com/pls/topic/lookup?ctx=javase15&id=GUID-8296D8E8-2B93-4B9A-856E-0A65AF9B8C66
> ```

### Cómo configurar las pistas de serialización

Configúralas con `@SerializationHint`.

```java
@NativeHint(serializables = @SerializationHint(types = { ArrayList.class, City.class, String.class, Integer.class, Number.class })) // Adding @SerializationHint
@SpringBootApplication
public class MybatisSpringNativeSampleApplication {
  // ...
}
```
### Cómo definir el filtro de serialización JEP-290

Define `-Djdk.serialFilter` (propiedad de sistema) en `buildArgs` de `native-maven-plugin` en `pom.xml`.

p. ej.)

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

# Ejemplos

Proporciona ejemplos para ejecutar MyBatis en spring-native.

| Nombre                                                | Descripción                                                                                                                      |
|-------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `mybatis-spring-native-sample-simple`                 | Aplicación de ejemplo muy simple con mapper dirigido por anotaciones (`@Select`/`@Insert`/etc...)                                |
| `mybatis-spring-native-sample-xml`                    | Aplicación de ejemplo muy simple con mapper dirigido por fichero XML                                                             |
| `mybatis-spring-native-sample-sqlprovider`            | Aplicación de ejemplo muy simple con mapper dirigido por SQL provider (`@SelectProvider`/`@InsertProvider`/etc...)               |
| `mybatis-spring-native-sample-scan`                   | Aplicación de ejemplo que usa las anotaciones `@MapperScan` y `@MyBatisResourcesScan`                                            |
| `mybatis-spring-native-sample-dao`                    | Aplicación de ejemplo con patrón DAO (sin interfaz mapper)                                                                       |
| `mybatis-spring-native-sample-thymeleaf`              | Aplicación de ejemplo que usa `mybatis-thymeleaf`                                                                                |
| `mybatis-spring-native-sample-thymeleaf-sqlgenerator` | Aplicación de ejemplo que usa `SqlGenerator` de `mybatis-thymeleaf` sin los módulos `mybatis` y `mybatis-spring`                 |
| `mybatis-spring-native-sample-velocity`               | Aplicación de ejemplo que usa `mybatis-velocity`                                                                                 |
| `mybatis-spring-native-sample-freemarker`             | Aplicación de ejemplo que usa `mybatis-freemarker`                                                                               |
| `mybatis-spring-native-sample-cache`                  | Aplicación de ejemplo con la caché de segundo nivel integrada                                                                    |
| `mybatis-spring-native-sample-configuration`          | Aplicación de ejemplo que personaliza la configuración de MyBatis con propiedades (`application.properties`)                     |
| `mybatis-spring-native-sample-dynamic-sql`            | Aplicación de ejemplo que usa `mybatis-dynamic-sql`                                                                              |

# Traducciones

Los usuarios pueden leer MyBatis-Spring-Native en las siguientes traducciones:

<ul class="i18n">
  <li class="en"><a href="./../index.html">English</a></li>
  <li class="es"><a href="./../es/index.html">Español</a></li>
  <li class="zh"><a href="./../zh/index.html">简体中文</a></li>
  <li class="ko"><a href="./../ko/index.html">한국어</a></li>
</ul>
