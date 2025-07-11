# Configuración de MySQL

![raw](../../../images/bbdd/mysql/mysql.png)

Guía detallada para configurar y administrar MySQL en contenedores Docker.

## Tabla de Contenidos

- [Configuración de MySQL](#configuración-de-mysql)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Prerequisitos](#prerequisitos)
  - [Paso 1: Configuración con Docker Compose](#paso-1-configuración-con-docker-compose)
  - [Paso 2: Iniciar el contenedor MySQL](#paso-2-iniciar-el-contenedor-mysql)
    - [Gestión rápida con Makefile](#gestión-rápida-con-makefile)
  - [Paso 3: Conexión y operaciones básicas](#paso-3-conexión-y-operaciones-básicas)
    - [Conectarse al servidor MySQL](#conectarse-al-servidor-mysql)
    - [Comandos básicos de MySQL](#comandos-básicos-de-mysql)
  - [Paso 4: Gestión de bases de datos](#paso-4-gestión-de-bases-de-datos)
    - [Crear una base de datos](#crear-una-base-de-datos)
    - [Eliminar una base de datos](#eliminar-una-base-de-datos)
  - [Paso 5: Gestión de usuarios y permisos](#paso-5-gestión-de-usuarios-y-permisos)
    - [Crear un nuevo usuario](#crear-un-nuevo-usuario)
    - [Otorgar permisos](#otorgar-permisos)
  - [Paso 6: Importar y exportar datos](#paso-6-importar-y-exportar-datos)
    - [Importar un archivo SQL](#importar-un-archivo-sql)
    - [Exportar una base de datos](#exportar-una-base-de-datos)
    - [Conectar con cliente MySQL externo](#conectar-con-cliente-mysql-externo)
  - [Resolución de problemas](#resolución-de-problemas)
    - [Ver logs del contenedor](#ver-logs-del-contenedor)
    - [Conectar directamente al contenedor](#conectar-directamente-al-contenedor)
    - [Ver variables de configuración](#ver-variables-de-configuración)
  - [Enlaces útiles](#enlaces-útiles)
  - [Recomendaciones adicionales](#recomendaciones-adicionales)
  - [Volver a su ficha](#volver-a-su-ficha)

## Prerequisitos

Antes de comenzar, asegúrate de lo siguiente:

- **Docker** está instalado y en ejecución.
- **Docker Compose** está instalado (se recomienda la versión 2.29.x o superior).
- **Make** (opcional, pero recomendado para simplificar los comandos).

> Puedes utilizar el script de `docker_install.sh` de la carpeta `/resources` para instalar ambas cosas en WSL/Ubuntu.

## Paso 1: Configuración con Docker Compose

Crea un archivo `docker-compose.yml` con la siguiente configuración. Puedes encontrar los archivos de configuración en el directorio `/resources/bbdd/mysql/`:

docker-compose.yml

```yaml
services:
  mysql:
    image: mysql:8.4.5
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mydatabase
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init:/docker-entrypoint-initdb.d
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 5s
      retries: 10

volumes:
  mysql_data:
```

## Paso 2: Iniciar el contenedor MySQL

Inicia los contenedores de MinIO y el cliente usando Docker Compose. Este comando descarga las imágenes (si no están descargadas), crea los volúmenes y arranca los servicios.

```bash
docker-compose up -d
```

- **Explicación**:
  - El flag `-d` ejecuta los contenedores en segundo plano (modo detach).
  - Verifica que los contenedores estén en ejecución:

    ```bash
    docker ps
    ```

    Deberías ver un contenedor: `mysql` con el puerto `3306` mapeado.

### Gestión rápida con Makefile

En la carpeta `resources/bbdd/mysql/` tienes un `Makefile` preparado para gestionar MySQL fácilmente:

```makefile
MYSQL_COMPOSE=docker-compose.yml

up:
    docker-compose -f $(MYSQL_COMPOSE) up -d

down:
    docker-compose -f $(MYSQL_COMPOSE) down

logs:
    docker-compose -f $(MYSQL_COMPOSE) logs -f

restart: 
    docker-compose -f $(MYSQL_COMPOSE) restart
```

Ejecuta los siguientes comandos desde esa carpeta:

- Levantar MySQL:

  ```bash
  make up
  ```

- Parar MySQL:

  ```bash
  make down
  ```

- Reiniciar MySQL:

  ```bash
  make restart
  ```

- Ver logs:

  ```bash
  make logs
  ```

> **Nota:** Para usar los comandos `make`, asegúrate de tener instalado el paquete `make`.
> Puedes instalarlo en sistemas basados en Debian/Ubuntu con:

```bash
sudo apt install make
```

## Paso 3: Conexión y operaciones básicas

### Conectarse al servidor MySQL

```bash
docker exec -it mysql mysql -u root -p
```

### Comandos básicos de MySQL

- Mostrar bases de datos:

  ```sql
  SHOW DATABASES;
  ```

- Usar una base de datos:

  ```sql
  USE nombre_base_datos;
  ```

- Mostrar tablas:

  ```sql
  SHOW TABLES;
  ```

## Paso 4: Gestión de bases de datos

### Crear una base de datos

```sql
CREATE DATABASE nombre_base_datos;
```

### Eliminar una base de datos

```sql
DROP DATABASE nombre_base_datos;
```

## Paso 5: Gestión de usuarios y permisos

### Crear un nuevo usuario

```sql
CREATE USER 'usuario'@'%' IDENTIFIED BY 'contraseña_segura';
```

### Otorgar permisos

```sql
GRANT ALL PRIVILEGES ON nombre_base_datos.* TO 'usuario'@'%';
FLUSH PRIVILEGES;
```

## Paso 6: Importar y exportar datos

### Importar un archivo SQL

1. Copia el archivo al contenedor:

   ```bash
   docker cp archivo.sql mysql:/tmp/
   ```

2. Importa la base de datos:

   ```bash
   docker exec -i mysql mysql -u root -p nombre_base_datos < archivo.sql
   ```

### Exportar una base de datos

```bash
docker exec mysql mysqldump -u root -p nombre_base_datos > backup.sql
```

### Conectar con cliente MySQL externo

Puedes conectar cualquier cliente MySQL a:

- Host: `localhost`
- Puerto: `3306`
- Usuario: `root` o el usuario que hayas configurado
- Contraseña: La que hayas establecido en las variables de entorno

## Resolución de problemas

### Ver logs del contenedor

```bash
docker logs mysql
```

### Conectar directamente al contenedor

```bash
docker exec -it mysql bash
```

### Ver variables de configuración

```bash
docker exec -it mysql mysql -u root -p -e "SHOW VARIABLES;"
```

## Enlaces útiles

- [Documentación oficial de MySQL](https://dev.mysql.com/doc/)
- [MySQL en Docker Hub](https://hub.docker.com/_/mysql)
- [Guía de referencia de Docker Compose](https://docs.docker.com/compose/compose-file/)

## Recomendaciones adicionales

- Habilita SSL/TLS para conexiones seguras en producción.
- Configura índices para optimizar las consultas frecuentes.
- Usa `my.cnf` para ajustar parámetros como `innodb_buffer_pool_size` y mejorar el rendimiento.
- Integra con Kafka Connect usando conectores como `io.debezium.connector.mysql.MySqlConnector` para streaming de datos.
- Monitorea el rendimiento con herramientas como `MySQL Workbench` o `SHOW PROFILE`.

## Volver a su ficha

[Volver a la ficha de MySQL](../../tecnologias/bbdd/mysql.md)
