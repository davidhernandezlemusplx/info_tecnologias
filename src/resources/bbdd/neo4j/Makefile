NEO4J_COMPOSE=docker-compose.yml

up:
	docker compose -f $(NEO4J_COMPOSE) up -d

down:
	docker compose -f $(NEO4J_COMPOSE) down

restart:
	docker compose -f $(NEO4J_COMPOSE) restart

logs:
	docker compose -f $(NEO4J_COMPOSE) logs -f
