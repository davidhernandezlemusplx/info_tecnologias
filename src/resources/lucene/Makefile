LUCENE_COMPOSE=docker-compose.yml

up:
	docker compose -f $(LUCENE_COMPOSE) up -d

down:
	docker compose -f $(LUCENE_COMPOSE) down

restart:
	docker compose -f $(LUCENE_COMPOSE) restart

logs:
	docker compose -f $(LUCENE_COMPOSE) logs -f
