# 🔙 Event Manager - Back-end

Este projeto é o núcleo do sistema **Event Manager**, fornecendo uma API RESTful robusta e segura para o gerenciamento de eventos, funcionários e recursos. Construído com **Spring Boot**, ele segue princípios de arquitetura limpa e boas práticas de desenvolvimento.

## ✨ Funcionalidades Principais

- **Autenticação e Segurança:** Sistema robusto com **Spring Security** e OAuth2.
- **Gestão de Eventos (Parties):** Controle completo de eventos, incluindo criação, edição e listagem.
- **Gestão de Pessoal:** Administração de funcionários (`Employee`) e tipos de cargos (`EmployeeType`).
- **Controle de Recursos:** Gerenciamento de materiais (`Material`) necessários para os eventos.
- **Dashboard Analítico:** Endpoints dedicados para alimentar gráficos e métricas de desempenho.
- **Geração de Relatórios:** Exportação de dados e comprovantes em PDF.
- **Gestão de Usuários:** Controle de acesso e perfis de usuário.

## 🛠️ Stack Técnica e Decisões

O projeto foi desenvolvido focando em modernidade, tipagem forte e produtividade:

- **Java 17:** Versão LTS utilizada para garantir performance e recursos modernos da linguagem.
- **Spring Boot 3.5.3:** Framework base para desenvolvimento ágil e configuração simplificada.
- **Spring Security & OAuth2:** Camada de segurança para proteção de rotas e autenticação.
- **Spring Data JPA:** Abstração para persistência de dados (SQL).
- **MapStruct:** Mapeamento eficiente e type-safe entre Entidades e DTOs.
- **Lombok:** Redução de código boilerplate (Getters, Setters, Builders).
- **PostgreSQL / MySQL:** Suporte a bancos de dados relacionais robustos.
- **Docker:** Suporte a containerização (Dockerfile incluso).

## 🚀 Como rodar o projeto

### Pré-requisitos
- **Java JDK 17** ou superior.
- **Maven** instalado e configurado.
- Banco de dados **MySQL** ou **PostgreSQL** rodando.

### Instalação e Execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/JuanRNS/event-manager.git
   cd event-manager-back-end
   ```

2. **Configure o Banco de Dados:**
   Verifique o arquivo `src/main/resources/application.properties` (ou `.yml`) e ajuste as credenciais do banco de dados se necessário.

3. **Compile o projeto:**
   ```bash
   mvn clean install
   ```

4. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```
   
   A API estará disponível em: `http://localhost:8080`

## 📂 Estrutura do Projeto

O código está organizado de forma a separar as regras de negócio de detalhes de implementação, facilitando a manutenção e testes.

### `src/main/java/com/example/eventmanagerbackend`

- **`domain`**: Contém o núcleo da lógica de negócios, independente de frameworks externos o máximo possível.
  - **`entities`**: Modelos de dados persistentes (JPA).
  - **`dtos`**: Objetos de transferência de dados para API.
  - **`enums`**: Constantes e tipagens do domínio.

- **`infrastructure`**: Implementações técnicas e adaptações para o mundo externo (Banco de dados, API, Segurança).
  - **`controllers`**: Pontos de entrada da API REST (endpoints).
  - **`services`**: Implementação das regras de negócio e orquestração.
  - **`repositories`**: Interfaces de acesso a dados (Spring Data).
  - **`security`**: Configurações de segurança e filtros de autenticação.
  - **`mappers`**: Conversores entre DTOs e Entidades.
  - **`exceptions`**: Tratamento global de erros.

## 🔌 Endpoints Principais

| Recurso | Descrição | Controller |
|---------|-----------|------------|
| `/api/auth` | Login e autenticação | `AuthController` |
| `/api/party` | Gestão de festas/eventos | `PartyController` |
| `/api/employee` | Gestão de funcionários | `EmployeeController` |
| `/api/dashboard` | Dados para dashboards | `DashBoardController` |
| `/api/pdf` | Geração de PDFs | `PdfController` |

---
*Desenvolvido com foco em escalabilidade e clean code.*
