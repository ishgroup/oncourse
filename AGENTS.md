# AGENTS.md

Guidance for AI coding agents working in this repository. Keep it short and accurate; update it when conventions change.

## Project overview

onCourse is an open-source (AGPL-3.0) ERP / CRM / ecommerce system for training providers. It's a Gradle multi-project build:

| Module | What it is |
|---|---|
| `server/` | Backend (Java + Groovy, Cayenne ORM, Guice DI, Jetty). Entry point: `server/src/main/java/ish/oncourse/server/AngelServer.java` |
| `server-api/` | OpenAPI (Swagger) definitions. **The source of truth for the REST API** and for generated client/server models |
| `client-html/` | React web client (TypeScript, Redux, redux-observable, MUI) |
| `types/` | Shared types module |
| `buildSrc/` | Gradle build logic: swagger codegen, AQL grammar, API docs, test datasets |
| `api-test/`, `selenium-test/` | API and browser integration tests |
| `api-doc/` | API documentation generation |

## Build and run

```bash
./gradlew -x test server:runServer        # run the server from source
./gradlew server-api:swagger              # regenerate API models (server DTOs + client-html/build/generated-sources/swagger-js)
./gradlew client-html:jest                # client tests as CI runs them (don't run by default, see Tests)
./gradlew server:test -Duser.language=en -Duser.country=AU   # server tests as CI runs them
```

Inside `client-html/` (after codegen has run at least once):

```bash
npm start                                 # dev server (webpack.dev.config.js)
npx jest path/to/file.test.ts             # run a single test file (only when asked)
npx tsc --noEmit -p tsconfig.json         # type check
npx eslint src/js/path/to/file.tsx        # lint
```

- `client-html/build/generated-sources/` (swagger-js, aql-parser, aql-model) is **generated** — never edit it. If imports from `@api/model` or `@aql/*` fail to resolve, run `./gradlew client-html:queryGrammar`.
- Server tests assume the `Australia/Sydney` timezone and an AU locale.

## Changing the API

1. Edit the YAML under `server-api/src/main/resources/` (`server-api.yaml`, `path/`, `def/entity/`, `def/enum/`, …).
2. Run `./gradlew server-api:swagger` to regenerate models.
3. Implement on the server in `server/src/main/groovy/ish/oncourse/server/api/v1/service/impl/*ApiImpl.groovy` (business logic usually lives in `server/src/main/java/ish/oncourse/server/api/service/*ApiService.java` or Groovy services).
4. On the client, call it through a service class that wraps the generated `XxxApi` (see below).

## Client (`client-html/src/js`) conventions

Layout:

- `containers/<area>/` — features. Entity screens live in `containers/entities/<entity>/` with subfolders `actions/`, `epics/`, `components/`, `services/`, `reducers/`, `utils/`, `constants/`.
- `common/` — shared `actions`, `epics`, `reducers`, `services`, `components`, `utils`, `api/fetch-errors-handlers`.
- `model/` — client-side TS models extending the generated API types.
- `reducers/state.ts` — root `State` type; `EpicRoot.ts` — root epic registration.

Patterns to follow:

- **Actions**: action type constants + plain action creators in `actions/index.ts`. Request-style types use `_toRequestType("verb/resource/...")` from `common/actions/ActionUtils`.
- **Async logic goes in epics**, built with `EpicUtils.Create(request)` (or `CreateWithTimeout` for delayed/polling flows). A request defines `type`, `getData`, `processData` (returns an array of actions), and optionally `processError` (usually `FetchErrorHandler(response, "message")`). One epic per file named `EpicXxx.ts`; register new epics in the folder's `epics/index.ts`.
- **Services**: a class wrapping the generated API (`new XxxApi(new DefaultHttpService())`), exported as a singleton `export default new XxxService()`. Don't call axios directly from components or epics.
- **Translations**: user-visible strings go through `import $t from '@t'`; add keys to `client-html/translate/translation_AU.json` and `translation_GB.json`.
- **Styling/UI**: prefer components and helpers from `ish-ui` (`makeAppStyles`, `AppTheme`, inputs) and MUI. Forms use `redux-form`.
- **Path aliases**: `@api/model`, `@aql/*`, `@t`, `@src` (see `tsconfig.json` and the jest `moduleNameMapper` in `package.json`). Existing code also uses relative imports — match the file you're in.
- TypeScript is non-strict (`strictNullChecks: false`, `noImplicitAny: false`); don't introduce strict-mode-only refactors in unrelated code.
- Style: ESLint airbnb-typescript (`client-html/.eslintrc.js`); `arrow-parens: as-needed`, camelCase.

## Tests

- Client tests live in `client-html/src/tests/`, mirroring `src/js` (e.g. `tests/entities/messages/EpicSendMessage.test.ts`). Jest + SWC + jsdom + Testing Library.
- Epic tests use the `DefaultEpic({ action, epic, store, processData })` helper from `src/tests/common/Default.Epic.ts`: provide the incoming action, a mocked store state, and the expected output actions. Component tests have helpers in `src/tests/common/Default.Components.tsx` and `MockedEditView.Components.tsx`.
- API calls in client tests are mocked with `axios-mock-adapter` (see `src/tests/TestEntry.tsx`, `src/tests/common/Default.Components.tsx` and the mock server in `src/dev/`).
- Server tests are Groovy, in `server/src/test/groovy`, with fixtures in `server/src/test/resources`.
- When you change behaviour, add or update a test next to the existing ones.
- **Don't run client tests locally** (`jest` / `client-html:jest`) as part of a task. They run automatically in GitHub Actions (`client-test.yml`) on every PR before merging into `main`. Run them only if the user asks.

## Source files

New source files start with the copyright header used by their neighbours, e.g. for client TS/TSX:

```ts
/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
```

Server Groovy/Java files use the AGPL header — copy it from an adjacent file.

## Git workflow

- Main branch: `main`. Tasks are tracked in Jira as `CS-<number>`.
- Branch names: `CS-<number>_Short_description` (e.g. `CS-558_Message_sent_but_error_occurs`).
- Commit messages: prefix with the ticket key, e.g. `CS-564 Updated ish-ui with fixed Tag input` or `CS-568 fix: <summary>`.
- CI (`.github/workflows/`) runs client tests, server tests and API tests on PRs to `main`; client CI skips server-only changes and vice versa.
- Never commit `.DS_Store`, `build/` output, `node_modules/`, or generated sources.
- Don't commit, push or open PRs unless asked.

## Agent working rules

- Read the surrounding code before editing and match its style, naming and comment density; keep diffs focused on the task.
- Don't hand-edit generated code; change the source (YAML spec, `.g4` grammar) and regenerate.
- Don't bump dependencies or touch `package-lock.json` unless the task requires it.
- Report honestly: if tests or type checks fail, or a step was skipped, say so.
