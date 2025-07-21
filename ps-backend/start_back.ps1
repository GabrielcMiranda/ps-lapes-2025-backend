docker compose -f ".\docker\docker-compose.yml" up -d

mvn spring-boot:run

docker compose -f ".\docker\docker-compose.yml" down