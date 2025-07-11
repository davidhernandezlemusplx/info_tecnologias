#!/bin/bash

# Instalar Docker en WSL (Ubuntu)

# Actualiza los paquetes de Ubuntu
sudo apt update && sudo apt upgrade -y

# Instala los paquetes necesarios para la instalación de Docker
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# Agrega la clave GPG de Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Agrega el repositorio de Docker
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Actualiza los paquetes nuevamente para incluir el repositorio de Docker
sudo apt update

# Instala Docker CE (Community Edition)
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Inicia el servicio de Docker
sudo service docker start

# Instalar Docker Compose en WSL
sudo apt install docker-compose -y
