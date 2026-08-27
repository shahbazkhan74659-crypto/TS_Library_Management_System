# CLAUDE.md

Rules and context for working on this repository (TS Library / `librarymanagement`).

## Project

A server-rendered Library Management System — Spring Boot + Thymeleaf + MySQL. Manages books and members, issues/returns books, and shows a dashboard of stats. See `README.md` for features, routes, and local setup.

- **Repo**: https://github.com/shahbazkhan74659-crypto/TS_Library_Management_System.git, branch `main`.
- **Local path**: `C:\JProject\librarymanagement`.
- **Stack**: Java 21, Spring Boot 3.5.16 (Web, Data JPA, Thymeleaf), MySQL via `mysql-connector-j`, Lombok, Maven (with wrapper).

## Relationship to the Portfolio project

This project is intended to become the **first real entry** in the Portfolio's `projects.Project` model (`C:\Portfolio`), once deployed — replacing one of that app's placeholder rows ahead of its own Phase 12 "Seeding Real Projects" (owner's explicit choice to do this early, 2026-08-26). Once the live Render URL exists, add a `Project` row there with `github_url` = this repo, `live_url` = the Render URL, tags reusing existing `Tag` rows (`Java`, `Spring Boot`, `Relational DB` already exist in that project's shared `Tag` table). Not done yet — blocked on this repo's own deployment finishing.

## Deployment — Render (free tier) + Aiven (free MySQL)

### Why this combination
Render's free tier only offers managed PostgreSQL, not MySQL, and even that free Postgres expires after 30 days. Rather than swap this app's database engine (real, if minor, architecture change), the owner chose to keep MySQL as-is and host it on **Aiven**, whose free MySQL tier is genuinely free-forever (1GB storage/RAM, no card, no expiry). Render still hosts the web service itself (free tier, spins down after 15 min idle).

### Code changes made for deploy-readiness (commits `b5dd982`, `f8ddeea` on `main`)
- `src/main/resources/application.properties`:
  - `spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/library_db}` — env var override, local default preserved (verified: local dev behavior is unchanged when the env var is unset).
  - `server.port=${PORT:8080}` — Render injects `PORT` at runtime; local default unchanged.
- `Dockerfile` (new) — multi-stage: `maven:3.9-eclipse-temurin-21` build stage → `eclipse-temurin:21-jre-alpine` runtime stage, packages `target/librarymanagement-0.0.1-SNAPSHOT.jar` as `app.jar`.
- `.dockerignore` (new).
- `render.yaml` (new) — Render Blueprint: `type: web`, `runtime: docker`, `plan: free`, `healthCheckPath: /`, three env vars (`SPRING_DATASOURCE_URL`, `DB_USERNAME`, `DB_PASSWORD`) marked `sync: false` so Render prompts for them in the dashboard rather than anything being committed.
- `README.md` — new "Deployment (Render, free tier)" section documenting the Aiven + Render steps.
- `.gitignore` — added `.env` (local credentials file, never committed).

### Local verification performed (before touching Render)
- `mvnw clean package` — builds clean.
- Booted the packaged jar with override env vars (`SPRING_DATASOURCE_URL`, `PORT` set to bogus test values) — confirmed both overrides take effect (bound to the overridden port, attempted to resolve the overridden host).
- Booted the jar with no overrides — confirmed it falls back to `localhost:3306`/port `8080` exactly as before (backward compatible).
- Booted the jar against the **real** Aiven database (via local `.env`) — HikariCP connected over SSL, Hibernate auto-created the schema (`ddl-auto=update`), and `/`, `/books`, `/about` all returned 200.
- Verified the schema directly against Aiven using **MySQL Shell** (`C:\Program Files\MySQL\MySQL Shell 8.0\bin\mysqlsh.exe`, already installed locally) — `SHOW TABLES` confirms `books`, `members`, `issued_books`, `returned_books` all exist with the expected columns. Tables were empty (fresh DB) as of the last check.
- **2026-08-27: Docker image build verified end-to-end.** Started Docker Desktop, ran `docker build -t ts-library:test .` — multi-stage build succeeded (Maven build stage: `BUILD SUCCESS`, jar repackaged; runtime stage: image exported cleanly). Then smoke-tested the built image itself (`docker run --env-file .env -e PORT=8080 -p 18080:8080 ts-library:test`) against the real Aiven database — container booted, connected over SSL, and `/`, `/books`, `/about` all returned 200. This confirms the Dockerfile/render.yaml setup is correct; the only remaining blocker is the corrupted env var in the Render dashboard itself (below).

### Aiven connection details
- Aiven has **two** connection tabs per service: **MySQL** (classic protocol, JDBC-compatible — use this) and **MySQLx** (X Protocol/document-store API — NOT JDBC-compatible, do not use for this app).
- Host: `mysql-library0921-shahbazkhan74659-c4a3.b.aivencloud.com`
- Port: `20743` (MySQL classic protocol tab — **not** `20747`, which is the MySQLx port)
- Database: `defaultdb`
- Username/password: stored locally in `.env` (gitignored) and in the Aiven console (Connect → Databases → MySQL tab → "Click to reveal password"). Not repeated here since this file may end up in a public repo.
- The JDBC URL requires `sslMode=REQUIRED` (camelCase — the driver property name; different from the `ssl-mode` query param shown in Aiven's raw `mysql://` connection URI).

### Render setup
- Web service created via the `render.yaml` Blueprint, plan Free, environment Docker.
- Required env vars (set in Render dashboard → Environment, not committed):
  - `SPRING_DATASOURCE_URL` = `jdbc:mysql://mysql-library0921-shahbazkhan74659-c4a3.b.aivencloud.com:20743/defaultdb?sslMode=REQUIRED`
  - `DB_USERNAME` = (see `.env` / Aiven console)
  - `DB_PASSWORD` = (see `.env` / Aiven console)
  - `PORT` is injected automatically by Render — do not set manually.

### Deploy history

**Attempt 1-2 (2026-08-26): FIXED.** `SPRING_DATASOURCE_URL` in the Render dashboard was corrupted — a stray Windows clipboard-history (Win+V) paste spliced `| OURCE_URL |` into the middle of the JDBC URL. Caused a Hibernate/HikariCP "claims to not accept jdbcUrl" error. Fixed by retyping the env var fresh in the Render dashboard (not pasted from clipboard history) and re-verifying `DB_USERNAME`/`DB_PASSWORD`.

**Attempt 3 (2026-08-27): FIXED.** Deploy logs showed the app successfully booting, connecting to Aiven over SSL, and serving real dashboard requests (proof the env var fix worked) — but Render still reported `==> Timed Out` followed by a graceful shutdown and `Failed`. Root cause: `render.yaml`'s `healthCheckPath: /` pointed at `HomeController.dashboard()`, which runs 5 DB round-trips (three counts + two full-table `findAll`s) against the remote Aiven DB on *every* health-check hit — measured at ~2.5s per request (mostly Aiven network latency), vs. 18ms for a DB-free response. Render's repeated health-check polling against that heavy endpoint blew the deploy's health-check timeout even though the app was actually healthy.

Fix (commit `0a2441e`): added a plain `GET /health` endpoint (`HealthController`, no DB access, ResponseEntity.ok("OK")) and changed `render.yaml`'s `healthCheckPath` to `/health`. Verified locally: rebuilt the Docker image, ran it against the real Aiven DB, confirmed `/health` returns 200 in ~18ms vs. `/`'s ~2.5s.

### Next steps
1. Trigger a fresh Render deploy (Blueprint or manual redeploy) picking up commit `0a2441e` — should now pass its health check quickly instead of timing out.
2. Once live: confirm `/`, `/books`, `/about`, `/health` on the Render URL; optionally add a free UptimeRobot monitor pinging it (Render free tier spins down after 15 min idle).
3. Consider whether the dashboard's per-request DB load (5 queries, 2 of them full-table `findAll`s) is worth optimizing later — it works, but every real visit to `/` still pays the ~2.5s Aiven round-trip cost the health check was avoiding.
4. Add the live Render URL + this repo URL as a real `Project` row in the Portfolio (see "Relationship to the Portfolio project" above).
