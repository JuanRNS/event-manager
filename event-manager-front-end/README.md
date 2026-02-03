# 📅 Event Manager - Front-end

Este projeto representa a interface de alta performance do sistema Event Manager, focado em uma experiência de usuário fluida e responsiva para gestão de eventos corporativos e sociais.

## ✨ Funcionalidades Principais
- **Dashboard de Eventos:** Visualização clara de eventos ativos e encerrados.
- **Formulários Inteligentes:** Criação de eventos com validações em tempo real utilizando Reactive Forms.
- **Integração Assíncrona:** Comunicação otimizada com a API via RxJS para garantir estados consistentes.
- **Design Responsivo:** Interface adaptável construída com Angular Material.

## 🛠️ Stack Técnica e Decisões
- **Angular 17+:** Utilização de *Standalone Components* para uma arquitetura mais leve e modular.
- **Angular Material:** Componentes de UI para uma experiência de usuário consistente.
- **RxJS:** Gerenciamento de fluxos de dados e chamadas HTTP assíncronas.

## 🚀 Como rodar o projeto

### Pré-requisitos
- Node.js (LTS)
- Angular CLI `^17.0.0`

### Instalação
1. Clone o repositório e acesse a pasta:
   ```bash
   cd event-manager-front-end
   ```

2. Instale as dependências:
   ```bash
   npm install
   ```

3. Execute o servidor de desenvolvimento:
   ```bash
   ng serve
   ```
4. Acesse `http://localhost:4200` no seu navegador.

## 📂 Estrutura do Projeto

O projeto segue uma arquitetura modular dividida principalmente em **Core** e **Features**, facilitando a escalabilidade e manutenção.

### `src/app/core`
Contém funcionalidades essenciais e reutilizáveis em toda a aplicação.
- **Components:** Componentes de UI compartilhados (e.g., Sidebar, Modais, Dashboard Widgets).
- **Services:** Serviços globais para comunicação com API e gerenciamento de estado.
- **Guards/Interceptors:** Proteção de rotas e manipulação de requisições HTTP.
- **Interfaces/Enums:** Definições de tipos e contratos de dados.

### `src/app/features`
Contém as visualizações (páginas) e lógica específica de cada funcionalidade de negócio.
- **Views:** Páginas principais da aplicação (e.g., Login, Calendário, Listas de Eventos).
- **Services:** Serviços específicos de cada feature (quando necessário).

## 🧩 Componentes Principais

### Core Components (`src/app/core/components`)
Componentes visuais que compõem a estrutura base da interface.
- **Sidebar:** Navegação lateral responsiva.
- **Dashboard:** Widgets e elementos visuais da tela inicial.
- **Modais:** Janelas de diálogo reutilizáveis para ações e alertas.
- **Form Group:** Componentes de formulário encapsulados para reuso e validação.
- **Summary:** Componentes para exibição de resumos e métricas.

### Feature Views (`src/app/features/views`)
As principais telas acessíveis pelo usuário.
- **Login / Register:** Fluxo de autenticação e cadastro de usuários.
- **Calendar:** Visualização de eventos em formato de calendário.
- **Dashboard Date/Week:** Visualizações temporais do dashboard.
- **Event Components:** Componentes específicos para exibição de detalhes de eventos.
- **Party Registration:** Formulários para criação e edição de eventos ("parties").
- **Party All List:** Listagem completa de eventos registrados.
