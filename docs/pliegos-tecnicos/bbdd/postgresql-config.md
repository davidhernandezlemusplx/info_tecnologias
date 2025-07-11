# Configuración de PostgreSQL

Guía rápida para configurar y administrar PostgreSQL en local o contenedores.

## Tabla de Contenidos

- [Configuración de PostgreSQL](#configuración-de-postgresql)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Ejemplo con Docker](#ejemplo-con-docker)
  - [Comandos útiles](#comandos-útiles)
    - [Acceder al contenedor PostgreSQL](#acceder-al-contenedor-postgresql)
    - [Acceder a PostgreSQL como usuario admin](#acceder-a-postgresql-como-usuario-admin)
    - [Crear una base de datos](#crear-una-base-de-datos)
    - [Crear un usuario y otorgar permisos](#crear-un-usuario-y-otorgar-permisos)
    - [Importar un dump SQL](#importar-un-dump-sql)
    - [Exportar una base de datos](#exportar-una-base-de-datos)
  - [Configuraciones clave](#configuraciones-clave)
  - [Recomendaciones](#recomendaciones)
  - [Enlaces útiles](#enlaces-útiles)
  - [Volver a su ficha](#volver-a-su-ficha)

## Ejemplo con Docker

```yaml
services:
  postgres:
    image: postgres:latest
    container_name: postgres
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
      POSTGRES_DB: myapp
    volumes:
      - postgres_data:/var/lib/postgresql/data
volumes:
  postgres_data:
```

## Comandos útiles

### Acceder al contenedor PostgreSQL

```bash
docker exec -it postgres bash
```

### Acceder a PostgreSQL como usuario admin

```bash
psql -U admin -d myapp
```

### Crear una base de datos

```sql
CREATE DATABASE ejemplo;
```

### Crear un usuario y otorgar permisos

```sql
CREATE USER usuario WITH PASSWORD 'contraseña';
GRANT ALL PRIVILEGES ON DATABASE ejemplo TO usuario;
```

### Importar un dump SQL

```bash
psql -U admin -d ejemplo < backup.sql
```

### Exportar una base de datos

```bash
pg_dump -U admin -d ejemplo > backup.sql
```

## Configuraciones clave

- **POSTGRES_USER**: Nombre de usuario para autenticación.
- **POSTGRES_PASSWORD**: Contraseña del usuario administrador.
- **POSTGRES_DB**: Nombre de la base de datos inicial.
- **ports**: Mapea el puerto 5432 del contenedor al host para acceso externo.
- **volumes**: Almacena datos de forma persistente en el volumen `postgres_data`.

## Recomendaciones

- Habilita SSL/TLS en producción para conexiones seguras.
- Configura índices para consultas frecuentes y optimiza el planificador de consultas.
- Usa `pg_hba.conf` para controlar el acceso a la base de datos.
- Integra con Kafka Connect usando conectores como `io.debezium.connector.postgresql.PostgresConnector` para streaming de datos.
- Monitorea el rendimiento con herramientas como `pg_stat_statements`.

## Enlaces útiles

- [Sitio oficial de PostgreSQL](https://www.postgresql.org/)
- [Documentación de PostgreSQL](https://www.postgresql.org/docs/)

## Volver a su ficha

[Volver a la ficha de PostgreSQL](../../tecnologias/bbdd/postgresql.md)
