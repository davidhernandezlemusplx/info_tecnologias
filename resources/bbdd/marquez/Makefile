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