# Docker + Docker Compose

![raw](../../images/interes/docker.png)

## Tabla de Contenidos

- [Docker + Docker Compose](#docker--docker-compose)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [1. Instalación de Docker y Docker Compose](#1-instalación-de-docker-y-docker-compose)
  - [2. Probar instalación con `docker pull`](#2-probar-instalación-con-docker-pull)
  - [3. Ver contenedores e imágenes](#3-ver-contenedores-e-imágenes)
  - [4. Eliminar contenedores e imágenes](#4-eliminar-contenedores-e-imágenes)
  - [5. Crear un Dockerfile (ejemplo Node.js)](#5-crear-un-dockerfile-ejemplo-nodejs)
  - [6. Crear un docker-compose.yml](#6-crear-un-docker-composeyml)
  - [7. Levantar / Eliminar Docker](#7-levantar--eliminar-docker)
    - [Iniciar contenedores](#iniciar-contenedores)
    - [Detener contenedores](#detener-contenedores)
  - [8. Script automático de instalación de Docker + Docker Compose](#8-script-automático-de-instalación-de-docker--docker-compose)
    - [¿Qué hace el script?](#qué-hace-el-script)
    - [Cómo usarlo](#cómo-usarlo)
    - [Notas](#notas)
  - [Volver al README](#volver-al-readme)

## 1. Instalación de Docker y Docker Compose

```bash
sudo apt update && sudo apt install -y ca-certificates curl gnupg lsb-release
```

Añade la clave GPG oficial de Docker:

```bash
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

Configura el repositorio oficial:

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

Instala Docker y Docker Compose:

```bash
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Añade tu usuario al grupo docker:

```bash
sudo usermod -aG docker $USER
```

Reinicia sesión o WSL, luego verifica:

```bash
docker version
docker compose version
```

## 2. Probar instalación con `docker pull`

```bash
docker pull alpine
docker images
```

## 3. Ver contenedores e imágenes

```bash
docker ps       # Contenedores activos
docker ps -a    # Todos los contenedores
docker images   # Imágenes descargadas
```

## 4. Eliminar contenedores e imágenes

```bash
docker rm <id|nombre>         # Eliminar contenedor detenido
docker rmi <id|nombre>        # Eliminar imagen
docker container prune        # Eliminar contenedores detenidos
docker image prune -a         # Eliminar todas las imágenes no usadas
```

## 5. Crear un Dockerfile (ejemplo Node.js)

```Dockerfile
FROM node:18
WORKDIR /app
COPY . .
RUN npm install
CMD ["npm", "start"]
```

## 6. Crear un docker-compose.yml

```yaml
services:
  app:
    build: .
    ports:
      - "3000:3000"
    depends_on:
      - postgres
    environment:
      - DB_HOST=postgres
      - DB_USER=postgres
      - DB_PASS=example
      - DB_NAME=miapp

  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: miapp
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: example
    ports:
      - "5432:5432"
```

## 7. Levantar / Eliminar Docker

### Iniciar contenedores

```bash
docker compose up -d
```

### Detener contenedores

```bash
docker compose down
```

## 8. Script automático de instalación de Docker + Docker Compose

Si prefieres automatizar toda la instalación de Docker y Docker Compose en WSL (Ubuntu), puedes usar el script `docker_install.sh`, de la carpeta `/resources`.

### ¿Qué hace el script?

- Instalar Docker en WSL (Ubuntu)
- Actualiza los paquetes de Ubuntu
- Instala los paquetes necesarios para la instalación de Docker
- Agrega la clave GPG de Docker
- Agrega el repositorio de Docker
- Actualiza los paquetes nuevamente para incluir el repositorio de Docker
- Instala Docker CE (Community Edition)
- Inicia el servicio de Docker
- Instalar Docker Compose en WSL
- Descarga Docker Compose

### Cómo usarlo

Dale permisos de ejecución:

```bash
chmod +x install_docker.sh
```

Ejecuta el script:

```bash
./install_docker.sh
```

### Notas

Este script está pensado para distribuciones Ubuntu dentro de WSL.

> El comando newgrp docker recarga los grupos del usuario actual sin cerrar sesión, pero a veces es necesario cerrar WSL y volver a entrar para que docker funcione sin sudo.

## [Volver al README](../README.md)
