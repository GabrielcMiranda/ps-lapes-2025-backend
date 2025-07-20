# stop.ps1
Write-Host "🛑 Encerrando containers Docker..."
docker compose -f ".\ps-backend\docker\docker-compose.yml" down

Write-Host "⚠️ Finalize manualmente os processos do backend e frontend se iniciados fora do Docker."