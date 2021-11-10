# Manejo de pagos adquirente LAPOS Y PAYSTORE (ms-acquirer-pos)

![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/alert_status)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/coverage)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/bugs)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/code_smells)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/sqale_rating)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/reliability_rating)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/security_rating)
![Build Status](https://sonar-badges.ops.wallet.prismamp.com/sonar/com.prismamp.todopago%3Ams-acquirer-pos/vulnerabilities)

## Stack 🛠️

 - java 11
 - kotlin 1.5
 - spring-boot 2
 - arrow-kt
 - redis
 - spek
 - kafka

## Build 🔧

Ejecuta éste comando para instalar las dependencias y buildear el proyecto:

```bash
$ ./gradlew build
```

## Run

Para correr el proyecto:

```bash
$ ./gradlew bootRun
```

El entorno de desarrollo corre sobre <http://localhost:8080>. Ejecuta una llamada GET de prueba en <http://localhost:8080/actuator>

## Unit Tests

Para correr los tests unitarios
```bash
$ ./gradlew test
```

## Config

El microservicio trae la configuración de [cloud-config](https://github.com/TodoPago/config). Pero es posible pisar propiedades de ser necesario.

Creando un `application.yml` podemos definir propiedades locales. ie:

```ỳaml
spring:
  kafka:
    bootstrap-servers: 'localhost:9092'
  redis:
    sentinel:
      master: 'redis-master'
      nodes: 'localhost:26379'


redis:
  master: redis-master
  nodes: localhost:26379
```

## Despliegue 🚀 

Para desplegar a QA debemos encolar el tag ingresar a [https://dev-tools.dev.wallet.prismamp.com/](https://dev-tools.dev.wallet.prismamp.com/), utilizando como credenciales el uss y pass de la vpn de prisma.
*Como encolar un tag*
1. Tomar el tag que tiene la imagen que queremos encolar, cada vez que se realiza un pr a la rama `develop` se generará un paquete con un tag, ver en la sección [releases](./releases). Copiar ese tag.
2. Ir al Jenkins [https://dev-tools.dev.wallet.prismamp.com/](https://dev-tools.dev.wallet.prismamp.com/) y entrar a la opcion encolar.
3. llenar los campos solicitados. En caso de tratarse de un fix de un incidente productivo hay que poner el selector en la opcion Hotfix.
