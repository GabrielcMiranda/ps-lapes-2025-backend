# start.ps1
Write-Host "📦 Subindo banco de dados com Docker Compose..."
docker compose -f ".\ps-backend\docker\docker-compose.yml" up -d

Start-Sleep -Seconds 5

Write-Host "🚀 Iniciando backend (Spring Boot)..."
Start-Process "mvn" -ArgumentList "spring-boot:run" -WorkingDirectory ".\ps-backend"

Start-Sleep -Seconds 10

Write-Host "⚛️ Iniciando frontend React..."
Start-Process "npm" -ArgumentList "start" -WorkingDirectory ".\PS-LAPES-FRONTEND-2025-main"
