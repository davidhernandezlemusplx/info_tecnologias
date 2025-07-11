MYSQL_COMPOSE=docker-compose.yml

# Iniciar los contenedores
up:
	docker-compose -f $(MYSQL_COMPOSE) up -d

# Detener y eliminar los contenedores
down:
	docker-compose -f $(MYSQL_COMPOSE) down

# Mostrar logs de los contenedors
logs:
	docker-compose -f $(MYSQL_COMPOSE) logs -f

# Reiniciar los contenedores
restart: 
	docker compose -f $(MYSQL_COMPOSE) restart