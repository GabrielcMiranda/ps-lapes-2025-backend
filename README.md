# PS LAPES 2025 - DEV

Descrição breve do projeto: este projeto é o principal entregável do processo seletivo do Laboratório de Pesquisa em Engenharia de Software (LAPES) do Centro Universitário do Estado do Pará (CESUPA).

---

## 🚀 Sumário

- [Pré-requisitos](#pré-requisitos)
- [Instalação Local](#instalação-local)
- [Configuração de Variáveis de Ambiente](#configuração-de-variáveis-de-ambiente)
- [Scripts Úteis](#scripts-úteis)
- [Contato](#contato)

---

## 📦 Pré-requisitos

### Backend
- ![Java](https://img.shields.io/badge/Java-17+-red?logo=openjdk&logoColor=white)  
- ![Maven](https://img.shields.io/badge/Maven-3.8+-brightgreen?logo=apachemaven&logoColor=white)  
- ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-%23336791?logo=postgresql&logoColor=white)  
- ![Docker](https://img.shields.io/badge/Docker-%230db7ed?logo=docker&logoColor=white)  

### Frontend
- [React](https://reactjs.org/)
- [Vite](https://vitejs.dev/)
- [TypeScript](https://www.typescriptlang.org/)
- [Tailwind CSS](https://tailwindcss.com/)
- [React Router](https://reactrouter.com/)
- [Axios](https://axios-http.com/)
- [Recharts](https://recharts.org/) (gráficos)
- [lucide-react](https://lucide.dev/) (ícones)
- [shadcn/ui](https://ui.shadcn.com/) (opcional, para componentes prontos)

---

## ⚙️ Instalação Local

1. Clone o repositório:

```bash
git clone https://github.com/GabrielcMiranda/ps-lapes-2025-backend.git
cd ps-lapes-2025-backend
```

2. Baixe as dependências do React:

```bash
npm install
```
## 🔐 Configuração de Variáveis de Ambiente
Crie os seguintes arquivos de acordo com seus respectivos .example dentro das seguintes pastas:

 - ps-lapes-2025-backend\ps-backend\src\main\resources -> app.key
 - ps-lapes-2025-backend\ps-backend\src\main\resources -> public.key
 - ps-lapes-2025-backend\ps-backend\src\main\resources -> application.properties
 - ps-lapes-2025-backend\ps-backend\docker -> .env

#### ATENÇÃO: é de extrema importância que os files criados tenham seus nomes escritos no .gitignore, para evitar o compartilhamento de dados sensíveis.

##  🧪 Scripts Úteis

 - ps-lapes-2025-backend\ps-backend\start_back.ps1 -> roda apenas o backend
 - ps-lapes-2025-backend\PS-LAPES-FRONTEND-2025-main\start_front.ps1 -> roda apenas o frontend
 - ps-lapes-2025-backend\start_all.ps1 -> roda backend e front end

##  🌐 Contatos

Desenvolvido por Gabriel Góes e Gabriel Miranda.


