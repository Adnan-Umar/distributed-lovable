<div align="center">

# Distributed Lovable

**An AI-powered, full-stack platform for building web apps through conversation — with live preview, collaborative workspaces, event-driven architecture powered by Kafka, real-time SSE streaming, and a cloud-native microservices backend.**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.1.1-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.8-3178C6?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Ready-326CE5?logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event--Driven-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-pgvector-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Caching-FF4438?logo=redis&logoColor=white)](https://redis.io/)
[![MinIO](https://img.shields.io/badge/MinIO-Object%20Storage-C72E49?logo=minio&logoColor=white)](https://min.io/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-2.0.0--M4-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Stripe](https://img.shields.io/badge/Stripe-Billing-635BFF?logo=stripe&logoColor=white)](https://stripe.com/)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Repository Layout](#repository-layout)
- [System Architecture](#system-architecture)
- [Request & Data Flows](#request--data-flows)
- [Backend Services](#backend-services)
- [Frontend Application](#frontend-application)
- [Shared Library (`common-lib`)](#shared-library-common-lib)
- [API Reference](#api-reference)
- [Infrastructure](#infrastructure)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Local Development](#local-development)
- [Environment Variables](#environment-variables)
- [Project Conventions](#project-conventions)

---

## Overview

**Distributed Lovable** is a monorepo that delivers a Lovable-style developer experience: users describe what they want in natural language, an AI assistant generates and edits project files, and the result is previewed live in the browser. The platform is built for scale with a **React + Vite** frontend and a **Spring Boot microservices** backend orchestrated through an API gateway, service discovery, and centralized configuration.

The repository is organized around two primary roots:

| Path | Purpose |
|------|---------|
| [`client/`](client/) | Browser UI — chat, code editor, file tree, live preview |
| [`server/`](server/) | Microservices, shared library, Docker Compose, and Kubernetes manifests |

---

## Key Features

| Area | Capabilities |
|------|-------------|
| **AI Code Generation** | Streaming chat with Spring AI, file-tree context, and tool-assisted file reads |
| **Project Workspace** | CRUD projects, file storage (MinIO), member invites, role-based access (Owner / Editor / Viewer) |
| **Live Preview** | Kubernetes runner pool + Redis-routed wildcard proxy for per-project preview URLs |
| **Authentication** | JWT-based auth via API gateway; signup/login flows |
| **Billing** | Stripe integration for subscriptions, checkout, and customer portal |
| **Event-Driven Storage** | Kafka saga for durable AI-generated file writes with idempotency |
| **Cloud Native** | Eureka discovery, Spring Cloud Config, K8s deployments, network policies |

---

## Tech Stack

### Frontend (`client/`)

| Layer | Technology |
|-------|------------|
| Framework | React 18, TypeScript 5.8 |
| Build | Vite 5, SWC |
| Routing | React Router 6 |
| State / Data | TanStack React Query |
| Styling | Tailwind CSS 3, shadcn/ui (Radix primitives) |
| Code Editor | CodeMirror 6 (`@uiw/react-codemirror`) |
| Layout | react-resizable-panels |
| Markdown | react-markdown, remark-gfm |
| Testing | Vitest, Testing Library |

### Backend (`server/`)

| Layer | Technology |
|-------|------------|
| Runtime | Java 21 |
| Framework | Spring Boot 4.0.5, Spring Cloud 2025.1.1 |
| AI | Spring AI 2.0.0-M4 (`ChatClient`, tools, advisors) |
| Gateway | Spring Cloud Gateway (reactive) |
| Discovery | Netflix Eureka |
| Config | Spring Cloud Config Server (Git-backed) |
| Persistence | Spring Data JPA, PostgreSQL + pgvector |
| Object Storage | MinIO |
| Messaging | Apache Kafka (Confluent) |
| Caching / Routing | Redis |
| K8s Client | Fabric8 Kubernetes Client |
| Payments | Stripe Java SDK |
| Mapping | MapStruct, Lombok |

---

## Repository Layout

```text
distributed-lovable/
│
├── client/                              # Frontend (Vite + React)
│   ├── public/                          # Static assets, error-catcher.js
│   ├── src/
│   │   ├── components/                  # Feature & UI components
│   │   │   ├── ui/                      # shadcn design-system primitives (40+)
│   │   │   ├── ChatPanel.tsx            # AI chat interface
│   │   │   ├── ChatEventRenderer.tsx    # Renders THOUGHT / FILE_EDIT events
│   │   │   ├── CodeEditor.tsx           # CodeMirror wrapper
│   │   │   ├── CodePanel.tsx            # Editor + file tabs layout
│   │   │   ├── FileTree.tsx             # Project file explorer
│   │   │   ├── FileTabs.tsx             # Open file tab bar
│   │   │   ├── PreviewPanel.tsx         # iframe live preview
│   │   │   ├── ShareDialog.tsx          # Member invite & roles
│   │   │   ├── LoginModal.tsx           # Auth modal
│   │   │   └── RuntimeErrorAlert.tsx    # Preview runtime errors
│   │   ├── hooks/                       # use-stream-parser, use-mobile, use-toast
│   │   ├── lib/                         # api.ts, types.ts, utils.ts
│   │   ├── pages/                       # Route-level views
│   │   │   ├── Index.tsx                # Auth redirect hub
│   │   │   ├── LoginModal route         # /login
│   │   │   ├── Signup.tsx               # /signup
│   │   │   ├── ProjectsDashboard.tsx    # /projects
│   │   │   ├── ProjectView.tsx          # /projects/:projectId
│   │   │   └── NotFound.tsx
│   │   ├── test/                        # Vitest setup & examples
│   │   ├── App.tsx                      # Router + providers
│   │   └── main.tsx                     # Entry point
│   ├── Dockerfile                       # Multi-stage NGINX production build
│   ├── package.json
│   ├── vite.config.ts
│   ├── tailwind.config.ts
│   └── vitest.config.ts
│
├── server/                              # Backend microservices
│   ├── account-service/                 # Auth, users, Stripe billing
│   ├── api-gateway/                     # Edge routing + JWT validation
│   ├── common-lib/                      # Shared DTOs, security, events, errors
│   ├── config-service/                  # Spring Cloud Config (port 8888)
│   ├── discovery-service/               # Eureka server (port 8761)
│   ├── intelligence-service/            # AI chat, streaming, Kafka producer
│   ├── workspace-service/               # Projects, files, members, K8s deploy
│   ├── k8s/                             # Kubernetes manifests
│   │   ├── infra/                       # Namespaces, ingress, network policies, runner pool
│   │   ├── services/                    # Microservice deployments
│   │   ├── stateful/                    # PostgreSQL, MinIO, Kafka, Redis
│   │   └── proxy/                       # Wildcard preview proxy (Node.js)
│   └── services.docker-compose.yml      # Local infrastructure stack
│
├── .gitignore
└── README.md
```

---

## System Architecture

The platform follows a classic **microservices + API gateway** pattern. Clients never talk to individual services directly — all traffic flows through the gateway, which validates JWTs and routes to the correct downstream service via Eureka service discovery.

```mermaid
flowchart TB
    subgraph Clients["Client Layer"]
        Browser["Web Browser<br/>(React + Vite)"]
    end

    subgraph Edge["Edge Layer"]
        GW["API Gateway<br/>Spring Cloud Gateway<br/>JWT Auth + CORS"]
    end

    subgraph Core["Platform Services"]
        CFG["Config Service<br/>:8888"]
        DISC["Discovery Service<br/>Eureka :8761"]
    end

    subgraph Domain["Domain Services"]
        ACC["Account Service<br/>Auth · Users · Stripe"]
        INT["Intelligence Service<br/>AI Chat · Streaming"]
        WS["Workspace Service<br/>Projects · Files · Deploy"]
    end

    subgraph Infra["Infrastructure"]
        PG[("PostgreSQL<br/>+ pgvector")]
        MINIO[("MinIO<br/>Object Storage")]
        KAFKA["Kafka<br/>Event Bus"]
        REDIS[("Redis<br/>Preview Routes")]
    end

    subgraph Preview["Preview Runtime"]
        PROXY["Wildcard Proxy<br/>Node.js"]
        RUNNER["Runner Pool<br/>Node + Vite pods"]
    end

    Browser --> GW
    GW --> ACC
    GW --> INT
    GW --> WS

    ACC & INT & WS --> DISC
    ACC & INT & WS --> CFG

    ACC --> PG
    INT --> PG
    INT --> KAFKA
    WS --> PG
    WS --> MINIO
    WS --> KAFKA
    WS --> REDIS

    WS --> RUNNER
    PROXY --> REDIS
    PROXY --> RUNNER
    Browser --> PROXY
```

### Layer Responsibilities

```mermaid
graph LR
    subgraph L1["Presentation"]
        A1["React SPA"]
        A2["CodeMirror Editor"]
        A3["SSE Chat Stream"]
    end

    subgraph L2["Edge & Security"]
        B1["API Gateway"]
        B2["JWT Validation"]
        B3["Route Prefixing"]
    end

    subgraph L3["Business Logic"]
        C1["Account"]
        C2["Intelligence"]
        C3["Workspace"]
    end

    subgraph L4["Cross-Cutting"]
        D1["common-lib"]
        D2["Config Server"]
        D3["Eureka"]
    end

    subgraph L5["Data & Events"]
        E1["PostgreSQL"]
        E2["MinIO"]
        E3["Kafka"]
        E4["Redis"]
    end

    L1 --> L2 --> L3
    L3 --> L4
    L3 --> L5
```

---

## Request & Data Flows

### AI Chat → File Storage Saga

When a user sends a chat message, the intelligence service streams an LLM response. Parsed `FILE_EDIT` events are published to Kafka; the workspace service persists files to MinIO/DB and acknowledges via a response topic.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant UI as React Client
    participant GW as API Gateway
    participant INT as Intelligence Service
    participant LLM as Spring AI / LLM
    participant WS as Workspace Service
    participant K as Kafka
    participant S as MinIO / DB

    User->>UI: Send chat message
    UI->>GW: POST /api/v1/intelligence/chat/stream
    GW->>INT: Forward (JWT validated)
    INT->>WS: GET file tree (internal)
    INT->>LLM: Stream prompt + tools + advisors
    LLM-->>INT: Token stream
    INT-->>UI: SSE (text/event-stream)

    Note over INT: On stream complete
    INT->>INT: Parse FILE_EDIT events
    INT->>K: file-storage-request-event-topic
    K->>WS: FileStoreRequestEvent
    WS->>S: Save file (idempotent saga)
    WS->>K: file-store-responses-event-topic
    K->>INT: FileStoreResponseEvent
```

### Live Preview Deployment

Deploy claims an idle runner pod from the pool, syncs project files from MinIO, starts Vite, and registers the hostname → pod mapping in Redis for the wildcard proxy.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant UI as React Client
    participant WS as Workspace Service
    participant K8s as Kubernetes API
    participant Pod as Runner Pod
    participant Redis as Redis
    participant Proxy as Wildcard Proxy

    User->>UI: Click Deploy / Preview
    UI->>WS: POST /projects/{id}/deploy
    WS->>K8s: Claim idle pod (runner-pool)
    WS->>Pod: Sync files via MinIO mc sidecar
    WS->>Pod: npm install && npm run dev
    WS->>Redis: SET route:project-{id}.domain → pod IP
    WS-->>UI: { previewUrl }
    User->>Proxy: GET project-{id}.previews.domain
    Proxy->>Redis: Lookup route
    Proxy->>Pod: Proxy HTTP/WS to :5173
    Pod-->>User: Live Vite preview
```

### Authentication Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as React Client
    participant GW as API Gateway
    participant ACC as Account Service

    User->>UI: Login / Signup
    UI->>GW: POST /api/v1/account/auth/login
    GW->>ACC: Forward (public route)
    ACC-->>UI: JWT + user info
    UI->>UI: Store token in localStorage

    Note over UI,GW: Subsequent requests
    UI->>GW: Authorization: Bearer {token}
    GW->>GW: Validate JWT signature & expiry
    GW->>ACC: Route to protected endpoints
```

---

## Backend Services

### Service Map

| Service | Port (local) | Database | Key Integrations |
|---------|-------------|----------|------------------|
| **config-service** | 8888 | — | Git config repo |
| **discovery-service** | 8761 | — | Eureka registry |
| **api-gateway** | (from config) | — | JWT, CORS, routing |
| **account-service** | (from config) | PostgreSQL | Stripe, JWT issuance |
| **intelligence-service** | (from config) | PostgreSQL | Spring AI, Kafka, Workspace client |
| **workspace-service** | (from config) | PostgreSQL | MinIO, Kafka, Redis, Fabric8 K8s |

> Service ports and routes are externalized to the [config server repository](https://github.com/Adnan-Umar/distributed-lovable-config-server) and loaded at startup via `spring.config.import`.

### `account-service`

Handles identity, authentication, and monetization.

```
account-service/src/main/java/.../
├── controller/
│   ├── AuthController.java          # /auth/signup, /auth/login
│   ├── BillingController.java       # Subscriptions, Stripe checkout/portal/webhooks
│   └── InternalAccountController.java  # /internal/v1/users, billing
├── entity/
│   ├── User.java
│   ├── Plan.java
│   └── Subscription.java
├── service/impl/
│   ├── AuthServiceImpl.java
│   └── SubscriptionServiceImpl.java
├── config/PaymentConfig.java        # Stripe SDK setup
└── security/AccountSecurityConfig.java
```

### `api-gateway`

Single entry point for all client traffic.

- **Global JWT filter** — validates Bearer tokens; skips configured public routes
- **CORS configuration** — cross-origin support for the SPA
- **Reactive routing** — load-balanced routes to Eureka-registered services
- Config loaded from config server: `configserver:${CONFIG_SERVER_URL:http://localhost:8888}`

### `intelligence-service`

AI brain of the platform.

```
intelligence-service/src/main/java/.../
├── controller/ChatController.java       # SSE streaming + history
├── service/impl/
│   ├── AiGenerationServiceImpl.java     # ChatClient orchestration
│   └── ChatServiceImpl.java
├── llm/
│   ├── tools/CodeGenerationTools.java   # @Tool read_files
│   ├── advisors/FileTreeContextAdvisor.java
│   ├── LlmResponseParser.java           # Parses FILE_EDIT events
│   └── PromptUtils.java
├── consumer/IntelligenceSagaResponseHandler.java
├── entity/                              # ChatSession, ChatMessage, ChatEvent, UsageLog
└── client/WorkspaceClient.java          # Feign-style internal calls
```

**Spring AI pipeline:** system prompt → user message → `FileTreeContextAdvisor` (injects project file tree) → `CodeGenerationTools.read_files` (tool calls) → streamed response → post-process into chat events → Kafka file storage saga.

### `workspace-service`

Owns project lifecycle, collaboration, and preview deployment.

```
workspace-service/src/main/java/.../
├── controller/
│   ├── ProjectController.java           # CRUD + deploy
│   ├── FileController.java              # File tree & content
│   ├── ProjectMemberController.java     # Invite, roles, remove
│   └── InternalWorkspaceController.java # Service-to-service APIs
├── entity/
│   ├── Project.java, ProjectFile.java
│   ├── ProjectMember.java, Preview.java
│   └── ProcessedEvent.java              # Kafka idempotency
├── consumer/FileStorageConsumer.java    # Kafka file write handler
├── service/impl/
│   ├── KubernetesDeploymentServiceImpl.java  # Runner pool + Redis routes
│   ├── ProjectServiceImpl.java
│   └── ProjectFileServiceImpl.java
├── config/
│   ├── StorageConfig.java               # MinIO client
│   └── KubernetesConfig.java            # Fabric8 client
└── security/SecurityExpressions.java   # @PreAuthorize helpers
```

### `discovery-service` & `config-service`

| Service | Role |
|---------|------|
| **discovery-service** | Eureka server on port **8761**; services register for dynamic lookup |
| **config-service** | Git-backed config server on port **8888**; centralizes YAML for all services |

---

## Frontend Application

### Routes

| Path | Component | Description |
|------|-----------|-------------|
| `/` | `Index` | Redirects to `/projects` or `/login` |
| `/login` | `LoginModal` | Username/password authentication |
| `/signup` | `Signup` | New user registration |
| `/projects` | `ProjectsDashboard` | Project list, create, delete |
| `/projects/:projectId` | `ProjectView` | Chat + code + preview workspace |

### Project Workspace Layout

The main editor view (`ProjectView`) uses resizable panels:

```text
┌─────────────────────────────────────────────────────────────┐
│  Header: project name · deploy · share · user menu          │
├──────────────────┬──────────────────────────────────────────┤
│                  │                                          │
│   Chat Panel     │   Code Panel  ↔  Preview Panel           │
│   (AI stream)    │   (FileTree + CodeMirror)  (iframe)      │
│                  │                                          │
└──────────────────┴──────────────────────────────────────────┘
```

### Key Frontend Modules

| Module | File | Responsibility |
|--------|------|----------------|
| API Client | `lib/api.ts` | REST calls, SSE chat stream, auth token management |
| Types | `lib/types.ts` | Shared TypeScript interfaces & enums |
| Stream Parser | `hooks/use-stream-parser.ts` | Parses SSE chunks from AI |
| Chat Events | `components/ChatEventRenderer.tsx` | Renders THOUGHT, MESSAGE, FILE_EDIT, TOOL_LOG |
| Preview | `components/PreviewPanel.tsx` | iframe with runtime error catching |

### API Base URL

Configured via environment variable at build time:

```bash
VITE_API_URL=http://api.lovable.local   # default in Dockerfile
```

All requests are prefixed through the gateway:

```text
/api/v1/account/...        → account-service
/api/v1/workspace/...      → workspace-service
/api/v1/intelligence/...   → intelligence-service
```

---

## Shared Library (`common-lib`)

Reusable backend module consumed by all domain services via Maven dependency.

| Package | Contents |
|---------|----------|
| `dto/` | `UserDto`, `PlanDto`, `FileTreeDto`, `FileNode` |
| `enums/` | `ProjectRole`, `ProjectPermission`, `ChatEventType`, `ChatEventStatus`, `MessageRole`, `SubscriptionStatus`, `PreviewStatus` |
| `event/` | `FileStoreRequestEvent`, `FileStoreResponseEvent` |
| `error/` | `GlobalExceptionHandler`, `ApiError`, `BadRequestException`, `ResourceNotFoundException` |
| `security/` | `JwtAuthFilter`, `JwtUserPrincipal`, `AuthUtil`, auto-configuration |

Auto-registered via `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`.

---

## API Reference

### Public Routes (via Gateway)

#### Authentication — `account-service`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/account/auth/signup` | Register new user |
| `POST` | `/api/v1/account/auth/login` | Login, returns JWT |

#### Billing — `account-service`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/account/api/me/subscription` | Current subscription |
| `POST` | `/api/v1/account/api/payments/checkout` | Stripe checkout session |
| `POST` | `/api/v1/account/api/payments/portal` | Stripe customer portal |
| `POST` | `/api/v1/account/webhooks/payments` | Stripe webhook handler |

#### Projects — `workspace-service`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/workspace/projects` | List user's projects |
| `POST` | `/api/v1/workspace/projects` | Create project |
| `GET` | `/api/v1/workspace/projects/{id}` | Get project details |
| `PATCH` | `/api/v1/workspace/projects/{id}` | Rename project |
| `DELETE` | `/api/v1/workspace/projects/{id}` | Delete project |
| `POST` | `/api/v1/workspace/projects/{id}/deploy` | Deploy live preview |

#### Files — `workspace-service`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/workspace/projects/{id}/files` | File tree listing |
| `GET` | `/api/v1/workspace/projects/{id}/files/content?path=` | File content |

#### Members — `workspace-service`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/workspace/projects/{id}/members` | List members |
| `POST` | `/api/v1/workspace/projects/{id}/members` | Invite member |
| `PATCH` | `/api/v1/workspace/projects/{id}/members/{userId}` | Update role |
| `DELETE` | `/api/v1/workspace/projects/{id}/members/{userId}` | Remove member |

#### AI Chat — `intelligence-service`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/intelligence/chat/stream` | SSE streaming chat (body: `{ message, projectId }`) |
| `GET` | `/api/v1/intelligence/chat/projects/{projectId}` | Chat history |

### Internal Routes (service-to-service)

| Service | Path | Purpose |
|---------|------|---------|
| account | `/internal/v1/users/{id}` | User lookup |
| account | `/internal/v1/users/by-email` | Email lookup |
| account | `/internal/v1/billing/current-plan` | Plan info |
| workspace | `/internal/v1/projects/{id}/files/tree` | File tree for AI context |
| workspace | `/internal/v1/projects/{id}/files/content` | File content for AI tools |
| workspace | `/internal/v1/projects/{id}/permissions/check` | Permission validation |

---

## Infrastructure

### Local Stack (`services.docker-compose.yml`)

| Service | Image | Host Port | Purpose |
|---------|-------|-----------|---------|
| **pgvector** | `pgvector/pgvector:0.8.1-pg18` | `9010` → 5432 | PostgreSQL + vector extension |
| **minio** | `quay.io/minio/minio:latest` | `9000` (API), `9001` (console) | Object storage |
| **kafka** | `confluentinc/confluent-local:7.5.0` | `9092`, `29092` | Event streaming |

**Default credentials (local only):**

| Service | User | Password |
|---------|------|----------|
| PostgreSQL | `user` | `password` |
| PostgreSQL DB | `pgvector-test` | — |
| MinIO | `minioadmin` | `minioadmin123` |

### Kafka Topics

| Topic | Producer | Consumer | Payload |
|-------|----------|----------|---------|
| `file-storage-request-event-topic` | intelligence-service | workspace-service | `FileStoreRequestEvent` |
| `file-store-responses-event-topic` | workspace-service | intelligence-service | `FileStoreResponseEvent` |

### Data Model (Core Entities)

```mermaid
erDiagram
    User ||--o{ Subscription : has
    Plan ||--o{ Subscription : defines

    Project ||--o{ ProjectFile : contains
    Project ||--o{ ProjectMember : has
    Project ||--o{ Preview : deploys

    ChatSession ||--o{ ChatMessage : contains
    ChatMessage ||--o{ ChatEvent : has

    User {
        bigint id PK
        string username
        string name
        string stripeCustomerId
    }

    Project {
        bigint id PK
        string name
        bigint ownerId
    }

    ProjectMember {
        bigint projectId FK
        bigint userId FK
        enum role
    }

    ChatSession {
        bigint projectId PK
        bigint userId PK
    }

    ChatMessage {
        bigint id PK
        enum role
        text content
        int tokenUsed
    }

    ChatEvent {
        bigint id PK
        enum type
        string filePath
        text content
        string sagaId
    }
```

---

## Kubernetes Deployment

### Namespaces

| Namespace | Purpose |
|-----------|---------|
| `lovable-core` | Microservices, stateful infra, ingress |
| `lovable-previews` | Runner pool pods for live previews |

### Manifest Structure

```text
server/k8s/
├── infra/
│   ├── namespaces.yaml              # lovable-core, lovable-previews + ConfigMap
│   ├── ingress.yaml                 # Frontend, API, wildcard preview routes
│   ├── runner-pool.yaml             # Idle Node.js + MinIO syncer pods
│   ├── core-network-policies.yaml   # Inter-service traffic rules
│   ├── preview-network-policies.yaml
│   └── core-dns-policy.yaml
├── services/
│   ├── api-gateway.yaml
│   ├── account-service.yaml
│   ├── workspace-service.yaml
│   ├── intelligence-service.yaml
│   └── config-service.yaml
├── stateful/
│   ├── pgvector.yaml
│   ├── minio.yaml
│   ├── kafka.yaml
│   └── redis.yaml
└── proxy/
    ├── index.js                     # Redis-backed wildcard reverse proxy
    ├── proxy-deployment.yaml
    ├── Dockerfile
    └── package.json
```

### Ingress Routes

| Host | Backend | Purpose |
|------|---------|---------|
| `lovable.local` / `localhost` | `lovable-frontend:80` | React SPA |
| `api.lovable.local` | `api-gateway:80` | REST + SSE API |
| `*.previews.lovable.local` | `lovable-proxy:80` | Per-project live previews |

### Preview Proxy

The Node.js proxy (`server/k8s/proxy/index.js`) resolves hostnames via Redis keys (`route:{hostname}` → pod IP) and forwards HTTP/WebSocket traffic to Vite dev servers on port 5173.

### Runner Pool

Each preview pod contains two containers:

1. **runner** — `node:20-alpine`, runs Vite dev server
2. **syncer** — `minio/mc`, syncs project files from MinIO into the shared workspace volume

The workspace service claims idle pods, labels them `status=busy`, syncs files, starts the dev server, and registers the route in Redis.

---

## Local Development

### Prerequisites

- **Node.js** 20+
- **Java** 21
- **Docker** & Docker Compose
- **Maven** (or use included `./mvnw` wrappers)

### 1. Start Infrastructure

```bash
cd server
docker compose -f services.docker-compose.yml up -d
```

### 2. Start Backend (recommended order)

```bash
# Terminal 1 — Config Server
cd server/config-service && ./mvnw spring-boot:run

# Terminal 2 — Discovery
cd server/discovery-service && ./mvnw spring-boot:run

# Terminal 3–5 — Domain Services
cd server/account-service && ./mvnw spring-boot:run
cd server/intelligence-service && ./mvnw spring-boot:run
cd server/workspace-service && ./mvnw spring-boot:run

# Terminal 6 — Gateway (start last)
cd server/api-gateway && ./mvnw spring-boot:run
```

> On Windows, use `mvnw.cmd` instead of `./mvnw`.

### 3. Start Frontend

```bash
cd client
npm install
npm run dev
```

The dev server typically runs at `http://localhost:5173`.

### Useful Commands

| Command | Location | Purpose |
|---------|----------|---------|
| `npm run build` | `client/` | Production build |
| `npm run lint` | `client/` | ESLint |
| `npm run test` | `client/` | Vitest |
| `./mvnw test` | any service | Unit tests |
| `docker compose -f services.docker-compose.yml down` | `server/` | Stop infra |

---

## Environment Variables

### Config Service

| Variable | Description |
|----------|-------------|
| `GITHUB_USERNAME` / `GITHUB_PASSWORD` | Git credentials for config repo (local) |
| `GIT_USERNAME` / `GIT_PASSWORD` | Git credentials (k8s profile) |

### API Gateway

| Variable | Default | Description |
|----------|---------|-------------|
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config server endpoint |

### Frontend (build-time)

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_API_URL` | `http://api.codingshuttle.in` | API gateway base URL |

### Preview Proxy

| Variable | Default | Description |
|----------|---------|-------------|
| `REDIS_URL` | `redis://localhost:6379` | Redis for route lookups |
| `PORT` | `80` | Proxy listen port |

### Kubernetes ConfigMap (`lovable-shared-config`)

| Key | Example | Description |
|-----|---------|-------------|
| `PREVIEW_DOMAIN` | `previews.adnan.in` | Base domain for preview URLs |
| `PREVIEW_NAMESPACE` | `lovable-previews` | K8s namespace for runners |
| `PROXY_PORT` | `80` | Preview URL port |
| `APP_FRONTEND_URL` | `http://localhost:5173` | Frontend origin |

---

## Project Conventions

| Convention | Detail |
|------------|--------|
| **Package naming** | `com.adnanumar.distributed_lovable.{service}` |
| **API versioning** | Gateway prefixes: `/api/v1/{service}/...` |
| **Auth** | JWT Bearer token in `Authorization` header |
| **Roles** | `OWNER`, `EDITOR`, `VIEWER` on project members |
| **Chat events** | `THOUGHT`, `MESSAGE`, `FILE_EDIT`, `TOOL_LOG` |
| **Idempotency** | Kafka sagas tracked via `ProcessedEvent` + `sagaId` |
| **Config** | Externalized to Git; not committed in service repos |

---

<div align="center">

Built with Spring Boot · Spring Cloud · Spring AI · React · Kubernetes

</div>
