# Web_Gen

Web_Gen generates complete web applications from a natural-language prompt. Describe the app you want, and it plans the project, scaffolds it, installs the dependencies, writes the files to disk, and lets you start, stop, modify and download the result from a web UI.

> **Status:** university research project. See [Current limitations](#current-limitations).

## Features

- **Prompt-to-project generation**: a multi-step LLM pipeline expands the prompt, designs the project layout, then generates the file contents.
- **Automated setup**: the generated scaffold and install commands are run for you, and the files are written to the project folder.
- **Run and stop generated apps**: the backend manages each app's process and tracks its status (`Generating`, `Starting`, `Started`, `Stopping`, `Stopped`, `Error`).
- **Natural-language modification**: describe a change, and the backend finds the most relevant files with vector similarity search, asks the LLM for edits, and applies them.
- **Project export**: download any generated project as a ZIP.
- **Web UI**: dashboard, project overview, new-project and modify pages, and a live view.

## Architecture

```
┌───────────────────┐   REST    ┌──────────────────────┐   HTTP    ┌──────────────────────┐
│ React + MUI (3001)│ ────────▶ │ Spring Boot / Kotlin │ ────────▶ │ Python LangGraph     │
│ TypeScript        │           │ (8080)               │           │ service (9000)       │
└───────────────────┘           └──────────┬───────────┘           └──────────┬───────────┘
                                           │                                  │
                               PostgreSQL + pgvector                     OpenAI chat model
                               (projects, code snippets,
                                embeddings)
```

| Part | Path | Stack |
|------|------|-------|
| Backend | [`Web_Gen/`](Web_Gen) | Kotlin, Spring Boot 3.4, Spring Data JPA, Spring AI, Java 21, Maven |
| Generation service | [`Web_Gen_Python/`](Web_Gen_Python) | Python, LangChain, LangGraph, OpenAI |
| Frontend | [`web_gen_frontend/`](web_gen_frontend) | React 19, TypeScript, Material UI, React Router 7 |
| Database | `Web_Gen/docker-compose.yml` | PostgreSQL 17 with the pgvector extension |

### How generation works

1. The frontend sends the project name and prompt to `POST /api/code/{projectName}/generate`.
2. The backend forwards the request to the Python service.
3. A LangGraph workflow runs three steps: **improve** (expand the prompt with features, pages and design ideas), **generate layout** (structured output with shell commands and file descriptions), and **generate files** (structured output with file contents). Conversation state is checkpointed per project thread.
4. The backend runs the generated scaffold and install commands, writes every file to disk, and stores each file as a code snippet in Postgres.
5. `POST /api/code/{projectId}/start` and `/terminate` start and stop the generated app.

### How modification works

`POST /api/code/{projectId}/modify` embeds the request, retrieves the most similar code snippets with a pgvector cosine-distance query, sends them to the LLM, and applies the returned edits. A replacement that doesn't match falls back to rewriting the whole file, and new files that collide with existing ones go through a conflict-resolution prompt.

## Getting started

### Prerequisites

- Java 21
- Python 3.10+ with `langchain`, `langgraph`, `langchain-openai` and `python-dotenv`
- Node.js and npm
- Docker (for PostgreSQL with pgvector)
- An OpenAI API key

The project is currently developed and tested on **Windows** (the scaffolding step shells out through `cmd /C`).

### 1. Database

```powershell
cd Web_Gen
docker compose up -d
```

This starts Postgres on port 5432 (database, user and password are all `web_gen`) and enables the `vector` extension. Hibernate creates the tables on first start.

### 2. Python generation service

Create `Web_Gen_Python/environment_variables.env`:

```
OPENAI_API_KEY=<your key>
```

```powershell
cd Web_Gen_Python
python webserver.py
```

### 3. Backend

Set these environment variables, then start the app:

| Variable | Meaning |
|----------|---------|
| `OPENAI_KEY` | OpenAI API key (used for embeddings and modification) |
| `GENERATED_PROJECT_PATH` | Folder where generated projects are written |
| `LANGCHAIN_IP_ADDRESS` | URL of the Python service, e.g. `http://localhost:9000` |

```powershell
cd Web_Gen
./mvnw spring-boot:run
```

The backend expects a user row with id `0` in the `users` table, because authentication is not implemented yet (see below).

### 4. Frontend

```powershell
cd web_gen_frontend
npm install
npm start
```

Open http://localhost:3001.

### Test mode

Setting `spring.ai.openai.use-test-responses=true` in `application.properties` makes the backend return canned responses instead of calling the LLM, which is useful for working on the UI without spending tokens.

## Current limitations

This is an actively changing project. Known gaps:

- **No real authentication.** The current user is a stub (always user id 0), and generated files go to a single shared `USER_ACCOUNT` folder.
- **Embeddings for newly generated files are placeholders.** Semantic search over a freshly generated project isn't meaningful until real embeddings are stored at generation time.
- **The Python service currently returns a canned response.** The LangGraph call in `webserver.py` is commented out for development and needs re-enabling for live generation.
- **Generated code is executed locally.** LLM-produced shell commands run on the host machine, so only run this in a development environment you trust.
- Windows-oriented; the Dockerfile is outdated and doesn't match the Maven build.
- Test coverage is minimal (a Spring context-load test).

## Roadmap

- Real user accounts and per-user project folders
- Store real embeddings when files are generated
- Sandbox execution of generated code and commands (for example in containers)
- Automatic error detection and repair when a generated app fails to start
- Cross-platform command execution
