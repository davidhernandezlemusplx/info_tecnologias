MONGO_COMPOSE=docker-compose.yml

# Iniciar el contenedor
up:
	docker-compose -f $(MONGO_COMPOSE) up -d

# Detener y eliminar el contenedor
down:
	docker-compose -f $(MONGO_COMPOSE) down

# Mostrar logs del contenedor
logs:
	docker-compose -f $(MONGO_COMPOSE) logs -f

# Reiniciar el contenedor
restart: 
	docker-compose -f $(MONGO_COMPOSE) restart