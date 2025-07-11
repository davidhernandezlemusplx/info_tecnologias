# Configuración de Marquez

![Marquez Logo](../../../images/bbdd/marquez/marquez.png)

Guía detallada para configurar y administrar Marquez en contenedores Docker.

## Tabla de Contenidos

- [Configuración de Marquez](#configuración-de-marquez)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Prerequisitos](#prerequisitos)
  - [Paso 1: Configuración con Docker Compose](#paso-1-configuración-con-docker-compose)
  - [Paso 2: Iniciar el contenedor](#paso-2-iniciar-el-contenedor)
    - [Gestión rápida con Makefile](#gestión-rápida-con-makefile)
  - [Paso 3: Acceso y operaciones básicas](#paso-3-acceso-y-operaciones-básicas)
    - [Acceso a la interfaz web](#acceso-a-la-interfaz-web)
    - [Uso de la API](#uso-de-la-api)
  - [Configuraciones recomendadas](#configuraciones-recomendadas)
    - [Ajustes de rendimiento](#ajustes-de-rendimiento)
    - [Seguridad](#seguridad)
  - [Solución de problemas](#solución-de-problemas)
    - [Verificar estado de los servicios](#verificar-estado-de-los-servicios)
    - [Acceder a los logs de PostgreSQL](#acceder-a-los-logs-de-postgresql)
    - [Verificar uso de recursos](#verificar-uso-de-recursos)
  - [Enlaces útiles](#enlaces-útiles)
  - [Volver a su ficha](#volver-a-su-ficha)

## Prerequisitos

Antes de comenzar, asegúrate de lo siguiente:

- **Docker** está instalado y en ejecución.
- **Docker Compose** está instalado (se recomienda la versión 2.29.x o superior).
- **Make** (opcional, pero recomendado para simplificar los comandos).

> Puedes utilizar el script de `docker_install.sh` de la carpeta `/resources` para instalar ambas cosas en WSL/Ubuntu.

## Paso 1: Configuración con Docker Compose

Crea un archivo `docker-compose.yml` con la siguiente configuración. Puedes encontrar los archivos de configuración en el directorio `/resources/bbdd/marquez/`:

docker-compose.yml

```yaml
services:
  marquez:
    image: marquezproject/marquez:latest
    container_name: marquez
    ports:
      - "5000:5000"  # API
      - "5001:5001"  # Web UI
    environment:
      MARQUEZ_DB_URL: jdbc:postgresql://postgres:5432/marquez
      MARQUEZ_DB_USERNAME: admin
      MARQUEZ_DB_PASSWORD: admin123
    restart: unless-stopped

volumes:
  marquez_data:
```

## Paso 2: Iniciar el contenedor

Inicia el contenedor de Marquez usando Docker Compose:

```bash
docker-compose up -d
```

### Gestión rápida con Makefile

Crea un archivo `Makefile` con los siguientes comandos útiles:

```makefile
MARQUEZ_COMPOSE=docker-compose.yml

# Iniciar el contenedor
up:
    docker-compose -f $(MARQUEZ_COMPOSE) up -d

# Detener y eliminar el contenedor
down:
    docker-compose -f $(MARQUEZ_COMPOSE) down

# Mostrar logs del contenedor
logs:
    docker-compose -f $(MARQUEZ_COMPOSE) logs -f

# Reiniciar el contenedor
restart:
    docker-compose -f $(MARQUEZ_COMPOSE) restart
```

Ejecuta los siguientes comandos desde esa carpeta:

- Levantar Marquez:

  ```bash
  make up
  ```

- Parar Marquez:

  ```bash
  make down
  ```

- Reiniciar Marquez:

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

## Paso 3: Acceso y operaciones básicas

### Acceso a la interfaz web

Una vez iniciados los contenedores, puedes acceder a la interfaz web de Marquez en:

- **URL Interfaz Web**: [http://localhost:5001](http://localhost:5001)
- **URL Interfaz Web**: [http://localhost:5000](http://localhost:5000)
- **URL Interfaz Web**: [http://localhost:5000/api/v1/namespaces](http://localhost:5000/api/v1/namespaces)

![Marquez Web](../../../images/bbdd/marquez/marquez-ui.png)

### Uso de la API

La API de Marquez está disponible en el puerto 5000. Algunos ejemplos de uso:

```bash
# Listar namespaces
curl http://localhost:5000/api/v1/namespaces

# Listar jobs
curl http://localhost:5000/api/v1/jobs

# Obtener información de un job específico
curl http://localhost:5000/api/v1/namespaces/NAMESPACE/jobs/JOB_NAME
```

## Configuraciones recomendadas

### Ajustes de rendimiento

```yaml
services:
  marquez:
    # ...
    environment:
      # Ajustar el tamaño del pool de conexiones
      MARQUEZ_DB_CONNECTION_POOL_SIZE: 10
      # Deshabilitar el registro detallado en producción
      MARQUEZ_LOG_LEVEL: INFO
```

### Seguridad

```yaml
services:
  marquez:
    # ...
    environment:
      # Habilitar autenticación básica
      MARQUEZ_AUTH_ENABLED: "true"
      MARQUEZ_AUTH_USERNAME: admin
      MARQUEZ_AUTH_PASSWORD: contraseña_segura
```

## Solución de problemas

### Verificar estado de los servicios

```bash
docker-compose ps
docker-compose logs marquez
```

### Acceder a los logs de PostgreSQL

```bash
docker-compose logs postgres
```

### Verificar uso de recursos

```bash
docker stats marquez marquez-postgres
```

## Enlaces útiles

- [Sitio oficial de Marquez](https://marquezproject.ai/)
- [Documentación de Marquez](https://marquezproject.ai/docs/)

## Volver a su ficha

[Volver a la ficha de Marquez](../../tecnologias/bbdd/marquez.md)
