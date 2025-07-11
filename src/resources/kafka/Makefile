KAFKA_COMPOSE=docker-compose.yml

up:
	docker compose -f $(KAFKA_COMPOSE) up -d

down:
	docker compose -f $(KAFKA_COMPOSE) down

restart:
	docker compose -f $(KAFKA_COMPOSE) restart

logs:
	docker compose -f $(KAFKA_COMPOSE) logs -f
