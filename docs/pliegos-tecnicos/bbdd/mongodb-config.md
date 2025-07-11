# Configuración de MongoDB

![MongoDB Logo](../../../images/bbdd/mongodb/mongodb.png)

Guía detallada para configurar y administrar MongoDB en contenedores Docker.

## Tabla de Contenidos

- [Configuración de MongoDB](#configuración-de-mongodb)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Prerequisitos](#prerequisitos)
  - [Paso 1: Configuración con Docker Compose](#paso-1-configuración-con-docker-compose)
  - [Paso 2: Iniciar el contenedor MongoDB](#paso-2-iniciar-el-contenedor-mongodb)
    - [Gestión rápida con Makefile](#gestión-rápida-con-makefile)
  - [Paso 3: Conexión y operaciones básicas](#paso-3-conexión-y-operaciones-básicas)
    - [Conectarse al servidor MongoDB](#conectarse-al-servidor-mongodb)
    - [Comandos básicos de MongoDB](#comandos-básicos-de-mongodb)
      - [Gestión de bases de datos](#gestión-de-bases-de-datos)
      - [Gestión de colecciones (tablas)](#gestión-de-colecciones-tablas)
  - [Configuraciones recomendadas](#configuraciones-recomendadas)
    - [Ajustes de rendimiento](#ajustes-de-rendimiento)
    - [Seguridad](#seguridad)
    - [Copia de seguridad](#copia-de-seguridad)
  - [Solución de problemas](#solución-de-problemas)
    - [Verificar estado del servicio](#verificar-estado-del-servicio)
    - [Conectarse directamente a MongoDB](#conectarse-directamente-a-mongodb)
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

Crea un archivo `docker-compose.yml` con la siguiente configuración. Puedes encontrar los archivos de configuración en el directorio `/resources/bbdd/mongodb/`:

docker-compose.yml

```yaml
services:
  mongodb:
    image: mongo:latest
    container_name: mongodb
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: admin123
      MONGO_INITDB_DATABASE: admin
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db

volumes:
  mongodb_data:
```

## Paso 2: Iniciar el contenedor MongoDB

Inicia el contenedor de MongoDB usando Docker Compose. Este comando descarga la imagen (si no está descargada), crea los volúmenes y arranca el servicio.

```bash
docker-compose up -d
```

### Gestión rápida con Makefile

Crea un archivo `Makefile` con los siguientes comandos útiles:

```makefile
MONGO_COMPOSE=docker-compose.yml

up:
    docker-compose -f $(MONGO_COMPOSE) up -d

down:
    docker-compose -f $(MONGO_COMPOSE) down

logs:
    docker-compose -f $(MONGO_COMPOSE) logs -f

restart: 
    docker-compose -f $(MONGO_COMPOSE) restart
```

Ejecuta los siguientes comandos desde esa carpeta:

- Levantar MongoDB:

  ```bash
  make up
  ```

- Parar MongoDB:

  ```bash
  make down
  ```

- Reiniciar MongoDB:

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

### Conectarse al servidor MongoDB

```bash
docker-compose exec mongodb mongosh -u admin -p admin123
```

- salida:

  ![MongoDB](../../../images/bbdd/mongodb/mongodb_cli.png)

### Comandos básicos de MongoDB

#### Gestión de bases de datos

```javascript
// Mostrar todas las bases de datos
show dbs

// Crear o cambiar a una base de datos (se crea al añadir datos)
use mi_base_datos

// Ver base de datos actual
db

// Eliminar base de datos actual (¡Cuidado! Esto borrará todos los datos)
db.dropDatabase()
```

#### Gestión de colecciones (tablas)

```javascript
// Mostrar todas las colecciones
show collections

// Crear una colección (se crea automáticamente al insertar datos)
// Insertar un documento en la colección 'usuarios'
db.usuarios.insertOne({
  nombre: "Juan Pérez",
  email: "juan@ejemplo.com",
  edad: 30,
  activo: true,
  fecha_registro: new Date()
})

// Insertar varios usuarios
db.usuarios.insertMany([
  { nombre: "Ana García", email: "ana@ejemplo.com", edad: 25 },
  { nombre: "Carlos López", email: "carlos@ejemplo.com", edad: 35 }
])

// Mostrar todos los usuarios
db.usuarios.find()

// Buscar usuarios mayores de 25 años
db.usuarios.find({ edad: { $gt: 25 } })

// Actualizar un usuario
db.usuarios.updateOne(
  { email: "juan@ejemplo.com" },
  { $set: { edad: 31 } }
)

// Eliminar un usuario
db.usuarios.deleteOne({ email: "juan@ejemplo.com" })

// Mostrar estadísticas de la colección
db.usuarios.stats()

// Eliminar la colección (¡Cuidado! Borra todos los datos)
db.usuarios.drop()
```

## Configuraciones recomendadas

### Ajustes de rendimiento

```yaml
services:
  mongodb:
    # ...
    command: >
      --wiredTigerCacheSizeGB 2
      --setParameter maxTransactionLockRequestTimeoutMillis=5000
      --setParameter ttlMonitorEnabled=true
```

### Seguridad

```yaml
services:
  mongodb:
    # ...
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=contraseña_segura
      - MONGO_INITDB_DATABASE=admin
    command: >
      --auth
      --bind_ip_all
      --enableFreeMonitoring off
```

### Copia de seguridad

```bash
# Crear copia de seguridad
mongodump --uri="mongodb://usuario:contraseña@localhost:27017" --out=/ruta/backup

# Restaurar copia de seguridad
mongorestore --uri="mongodb://usuario:contraseña@localhost:27017" /ruta/backup
```

## Solución de problemas

### Verificar estado del servicio

```bash
docker-compose ps
docker-compose logs mongodb
```

### Conectarse directamente a MongoDB

```bash
docker-compose exec mongodb mongosh -u admin -p admin123
```

### Verificar uso de recursos

```bash
docker stats mongodb
```

## Enlaces útiles

- [Documentación oficial de MongoDB](https://www.mongodb.com/docs/)
- [Guía de rendimiento de MongoDB](https://www.mongodb.com/docs/manual/administration/analyzing-mongodb-performance/)
- [Mejores prácticas de seguridad](https://www.mongodb.com/docs/manual/administration/security-checklist/)
- [Referencia de comandos MongoDB](https://www.mongodb.com/docs/manual/reference/command/)

## Volver a su ficha

[Volver a la ficha de MongoDB](../../tecnologias/bbdd/mongodb.md)
