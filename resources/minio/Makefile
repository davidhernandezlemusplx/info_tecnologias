MINIO_COMPOSE=docker-compose.yml

up:
	docker compose -f $(MINIO_COMPOSE) up -d

down:
	docker compose -f $(MINIO_COMPOSE) down

restart:
	docker compose -f $(MINIO_COMPOSE) restart

logs:
	docker compose -f $(MINIO_COMPOSE) logs -f
