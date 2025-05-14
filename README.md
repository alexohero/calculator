# Calculator API

Una API RESTful para realizar operaciones de calculadora con persistencia, autenticación de usuario y seguimiento del historial de operaciones.

## 1. Instrucciones de instalación del proyecto

### Prerrequisitos
- Java 21
- Maven
- MySQL 8.0

### Pasos para Instalar y Ejecutar

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/alexohero/calculator.git
   cd calculator
   ```

2. **Configurar la base de datos**
    - Crea una base de datos MySQL llamada `ravendb`
    - Actualiza la configuración de la base de datos en `src/main/resources/application.yml` si es necesario.

3. **Construir el proyecto**
   ```bash
   mvn clean install
   ```

4. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

   **Ejecutar como JAR ejecutable**
   ```bash
   # Construir el JAR ejecutable
   mvn clean package

   # Ejecutar el JAR
   java -jar target/calculator-0.0.1-SNAPSHOT.jar

   # Ejecutar con configuración personalizada
   java -jar target/calculator-0.0.1-SNAPSHOT.jar --spring.config.location=file:./custom-config/application.yml --logging.config=file:./custom-config/logback-spring.xml
   ```

5. **Acceder a la API**
    - La API estará disponible en `http://localhost:8080`
    - La documentación de Swagger UI está disponible en `http://localhost:8080/swagger-ui.html`

## 2. Configuración de la base de datos y API externa

### Configuración de la base de datos

La aplicación utiliza MySQL como base de datos principal. La configuración se encuentra en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ravendb
    username: user
    password: UserPass
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

La aplicación también admite la base de datos en memoria H2 para desarrollo/pruebas (comentada en el archivo de configuración).

### API Externa (Validación de Email)

#### a. API externa utilizada y motivo de la elección

La aplicación utiliza la API **mailboxlayer** para la validación del correo electrónico. Se eligió esta API porque
- Proporciona una validación completa del correo electrónico, incluida la comprobación de formato, la validación de registros MX y la detección de correo electrónico desechable.
- Tiene una interfaz REST sencilla y fácil de integrar.
- Ofrece una buena fiabilidad y rendimiento
- Proporciona información de validación detallada que ayuda a mejorar la experiencia del usuario.

#### b. Ejemplo de configuración de la clave API

La configuración de la API de mailboxlayer se encuentra en `application.yml`:

```yaml
mailboxlayer:
  key: 58328a60192649ce33cfcb4d06436b74
  url-api: http://apilayer.net/api/check
```

Para utilizar su propia clave API:
1. Regístrese en [mailboxlayer.com](https://mailboxlayer.com)
2. Obtenga su clave API
3. Sustituya la clave en la configuración

#### c. Lógica aplicada para determinar si un correo electrónico es aceptado

Un correo electrónico se considera válido si cumple TODOS los criterios siguientes:
1. Tiene un formato válido (sintaxis correcta)
2. Tiene registros MX válidos (el dominio puede recibir correos electrónicos)
3. NO es una dirección de correo electrónico desechable (se rechazan los servicios de correo electrónico temporales)

La lógica de validación se implementa en `EmailValidationServiceImpl.java`:

```java
public boolean isEmailValid(String email) {
    EmailValidationResponse response = validateEmail(email);

    boolean isValid = response.isFormatValid() && 
                      response.isMxFound() && 
                      !response.isDisposable();

    return isValid;
}
```

## 3. Ejemplos de uso con curl/httpie

### Registro de usuarios

```bash
# Usando curl
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alexdao","password":"secure_password","email":"alex_dao@example.com"}'

# Usando httpie
http POST http://localhost:8080/auth/register \
  username=alexdao \
  password=secure_password \
  email=alex_dao@example.com
```

### Login de Usuario

```bash
# Usando curl
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alexdao","password":"secure_password"}'

# Usando httpie
http POST http://localhost:8080/auth/login \
  username=alexdao \
  password=secure_password
```

### Realizar un cálculo

```bash
# Usando curl (reemplaza TU_TOKEN_JWT con el JWT del login)
curl -X POST "http://localhost:8080/calculate?operation=ADD&operandA=10&operandB=5" \
  -H "Authorization: Bearer TU_TOKEN_JWT"

# Usando httpie
http POST "http://localhost:8080/calculate?operation=ADD&operandA=10&operandB=5" \
  "Authorization: Bearer TU_TOKEN_JWT"
```

### Obtener historial de operaciones

```bash
# Usando curl
curl -X GET http://localhost:8080/history \
  -H "Authorization: Bearer TU_TOKEN_JWT"

# Usando httpie
http GET http://localhost:8080/history \
  "Authorization: Bearer TU_TOKEN_JWT"
```

### Obtener operación por ID

```bash
# Usando curl
curl -X GET http://localhost:8080/history/1 \
  -H "Authorization: Bearer TU_TOKEN_JWT"

# Usando httpie
http GET http://localhost:8080/history/1 \
  "Authorization: Bearer TU_TOKEN_JWT"
```

### Borrar operación por ID

```bash
# Usando curl
curl -X DELETE http://localhost:8080/history/1 \
  -H "Authorization: Bearer TU_TOKEN_JWT"

# Usando httpie
http DELETE http://localhost:8080/history/1 \
  "Authorization: Bearer TU_TOKEN_JWT"
```

## 4. Decisiones técnicas

### Spring Boot Framework
- **Por qué**: Se eligió Spring Boot por su ecosistema robusto, inyección de dependencias y configuración simplificada.
- **Beneficios**: Desarrollo rápido, amplias bibliotecas y buena integración con otras tecnologías.

### Autenticación JWT
- **Por qué**: JWT proporciona un mecanismo de autenticación sin estado que funciona bien con las API RESTful.
- **Beneficios**: No hay necesidad de almacenar información de sesión en el servidor, escalable y seguro cuando se implementa correctamente.

### Base de datos MySQL
- **Por qué**: MySQL fue elegida por su fiabilidad, rendimiento y amplia adopción.
- **Beneficios**: Sólida integridad de datos, cumplimiento de ACID y buen rendimiento para datos relacionales.

### Base de datos H2 (para desarrollo/pruebas)
- **Por qué**: H2 ofrece una opción de base de datos en memoria para desarrollo y pruebas.
- **Ventajas**: Inicio rápido, sin dependencias externas para pruebas y creación automática de esquemas.

### Mailboxlayer API para validación de correo electrónico
- **Por qué**: La validación externa proporciona una verificación del correo electrónico más fiable que los simples patrones regex.
- **Ventajas**: Validación completa que incluye registros MX y detección de correo electrónico desechable.

### Documentación Swagger/OpenAPI
- **Por qué**: Proporciona documentación interactiva de la API que siempre está sincronizada con el código.
- **Ventajas**: Exploración, pruebas e integración de API más sencillas para los clientes.

### Lombok
- **Por qué**: Reduce el código boilerplate para las clases modelo y el registro.
- **Ventajas**: Código más limpio, menos propenso a errores y mejor mantenibilidad.

### MapStruct
- **Por qué**: Mapeo seguro entre DTOs y entidades.
- **Ventajas**: Validación en tiempo de compilación, alto rendimiento y código de mapeo mantenible.

### Arquitectura en capas
- **Por qué**: Separación clara de preocupaciones con controladores, servicios y repositorios.
- **Ventajas**: Mejor mantenibilidad, comprobabilidad y escalabilidad.

### Java 21
- **Por qué**: Java 21 es una versión LTS (Long-Term Support) que ofrece mejoras significativas en rendimiento y nuevas características.
- **Ventajas**:
    - **Virtual Threads**: Mejora el rendimiento de aplicaciones concurrentes con miles de conexiones simultáneas.
    - **Pattern Matching for Switch**: Código más conciso y legible para estructuras de control complejas.
    - **Record Patterns**: Facilita la descomposición de datos estructurados.
    - **String Templates**: Simplifica la creación de cadenas con valores dinámicos.
    - **Sequenced Collections**: API mejorada para colecciones ordenadas.
    - **Mejor rendimiento del GC**: Recolector de basura Z (ZGC) mejorado para aplicaciones de alto rendimiento.
