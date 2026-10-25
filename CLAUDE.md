# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Event/party management system (festas, garçons, materiais, pagamentos). Monorepo with two independent apps, no root build:

- `event-manager-back-end/` — Spring Boot 3.5 / Java 17 / Maven (wrapper), PostgreSQL. Containerized via its `Dockerfile` (not currently deployed; the image needs `JWT_PRIVATE_KEY`/`JWT_PUBLIC_KEY` at runtime). `application.properties` is the base config; the `prod` profile (`application-prod.properties`) reads the keys from `PRIVATE_PEM`/`PUBLIC_PEM` instead and turns on Spring Security DEBUG logging. A local `application-dev.properties` is gitignored.
- `event-manager-front-end/` — Angular 21 standalone components, Angular Material, `angular-calendar`, Karma/Jasmine, PWA service worker. Not currently deployed (`vercel.json` with the SPA rewrite to `index.html` is left over from Vercel).

Domain language in UI, DTO names and messages is Portuguese (garçom = employee/waiter, festa = party, AGENDADA/REALIZADA statuses).

## Commands

Back-end (run inside `event-manager-back-end/`):

```bash
docker compose up -d              # local Postgres 15 (event_db / admin); needs POSTGRES_PASSWORD in .env (host port: POSTGRES_PORT, default 5432)
./mvnw spring-boot:run            # needs PGHOST PGPORT PGDATABASE PGUSER PGPASSWORD env vars (--server.port=N to change :8080)
./mvnw test                       # Mockito unit tests for services/security/controllers + a context-load test
./mvnw test -Dtest=ClassName#method
./mvnw clean package -DskipTests  # what the Dockerfile runs
```

`JAVA_HOME` must point to a JDK 17+. JWT signing keys come from `JWT_PRIVATE_KEY` / `JWT_PUBLIC_KEY` (PEM content or a resource location), defaulting to the gitignored `keys/private.pem` / `keys/public.pem`. Generate them once:

```bash
mkdir -p keys
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out keys/private.pem
openssl rsa -pubout -in keys/private.pem -out keys/public.pem
```

Front-end (run inside `event-manager-front-end/`, npm — `package-lock.json`):

```bash
npm start                         # ng serve on :4200
npm run build                     # production build
npm test                          # ng test (Karma, Chrome)
npx ng test --include src/app/core/services/toast.service.spec.ts
```

No linter or formatter script is configured in either app (only a Prettier override for Angular HTML in `package.json`).

## Back-end architecture

Package `com.example.eventmanagerbackend`:

- `domain/` — JPA `entities`, `enums`, and `dtos` (Java records, `*RequestDTO` / `*ResponseDTO`).
- `infrastructure/controllers` → `services` → `repositories`; MapStruct `mappers` (`componentModel = "spring"`, some fields deliberately ignored and set in the service). Exceptions in `infrastructure/exceptions` are translated by `RestExceptionHandler`.
- All routes are under `/api/...`.

Key cross-cutting behaviour:

- **Auth**: stateless JWT as an OAuth2 resource server. Tokens are signed/verified with the RSA keys above (`SecurityConfig`, `JwtService`). Password login is rate-limited per username in memory (`LoginAttemptLimiter`). Only `/api/user/login`, `/api/user/register` and `/api/auth/google` are public. Google login verifies a Google ID token (`AuthService`, `google.client.id`) and auto-registers unknown emails.
- **Multi-tenancy by user**: data is scoped to the logged-in user. Services call `UserService.getCurrentUser()` (resolves the JWT subject → `User` by username) and filter/own entities by it. New queries must keep this scoping.
- **`PartyScheduler`** (`@EnableScheduling` on the app class) flips parties from `AGENDADA` to `REALIZADA` daily at midnight.
- `PdfService` generates payment reports with PDFBox (uses `relatorioPagamento.png` from resources).
- `spring.jpa.hibernate.ddl-auto=update` — there are no migrations; Hibernate only adds schema, it never drops or alters columns.

## Front-end architecture

`src/app/`:

- `app.routes.ts` — routes; everything except `login`/`register` is lazy-loaded and protected by `authGuard`.
- `core/` — shared, feature-agnostic code: reusable components (`form-group` dynamic form/input/select, `sidebar`, `modais/*` dialogs opened via `MatDialog` with sizes from `core/config/modal.config.ts`, `toast` via `ToastService`), `interface/` (request/response types, `IRequest*` / `IResponse*`), `enums`, `directives` (input masks), `guards`, `utils`.
- `features/views/` — page components (dashboard, party, party-registration, provider-parties, event-components, calendar, login, register).
- `features/services/` — HTTP services. They extend `core/abstract/http.abstract.ts` (`HttpServiceAbstract`), which prefixes `environment.api`, sets JSON headers and attaches `Authorization: Bearer` from `localStorage['token']`. There is no HTTP interceptor — new API calls go through a service extending this class (most live in `ApiService`).
- Auth state is only the JWT in `localStorage`; `UserService.isLogged()` decodes it client-side and checks `exp`.

`environment.ts` (dev, `npm start`) points at `http://localhost:8080/api/`. The `production` configuration (`npm run build`) swaps in `environment.prod.ts` through `fileReplacements`, and that file points at a decommissioned Railway URL. CORS origins are set in `infrastructure/config/CorsConfig.java`.

`event-manager-front-end/README.md` is largely outdated boilerplate (mentions mock data, `ng build --prod`, ESLint) — trust the code over it.
