#!/bin/bash

echo "[INFO] Parando todos los contenedores en ejecución..."
docker ps -q | xargs -r docker stop

echo "[INFO] Eliminando todos los contenedores..."
docker ps -aq | xargs -r docker rm

echo "[INFO] Eliminando todas las imágenes..."
docker images -q | xargs -r docker rmi -f

echo "[INFO] Eliminando todos los volúmenes..."
docker volume ls -q | xargs -r docker volume rm

echo "[INFO] Limpiando la caché de build de Docker..."
docker builder prune -af

echo "[INFO] Limpieza completada."
