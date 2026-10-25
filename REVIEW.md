# REVIEW.md

Instructions for any reviewer of this repository — human or agent. Keep it short: if it grows past one screen, split by directory (a `REVIEW.md` in a subfolder overrides this one for that subtree).

## What failure looks like here

- **Back-end data access** — fails when a query or `findById` isn't scoped to `UserService.getCurrentUser()`: one user reads or edits another's parties, employees or materials (IDOR, fixed 2026-10).
- **Secrets** — JWT `.pem` keys and DB credentials were committed under `src/main/resources`; keys belong in env vars or the gitignored `keys/`.
- **Front-end specs** — components get renamed or moved without updating their `*.spec.ts`, so `ng test` stops compiling.

## Always flag as Important

- Behaviour change without a test that would fail if the change were reverted.
- Errors swallowed or turned into `null`/defaults at a system boundary.
- Any secret, token or `.env` content in the diff.
- Auth or permission checks removed, moved after the action, or bypassed.
- A new repository query or endpoint that doesn't filter by the current user.

## Do not flag

- Formatting and import order (no formatter is configured; not worth review time).
- Naming preferences, unless a name is actively misleading.
- Suggestions to add abstractions for code used in one place.

## Skip these paths

- `**/package-lock.json`, `**/dist/**`, `**/target/**`

## Evidence and output

- Every finding cites `file:line` and the code that proves it. No guesses from names.
- At most 3 nits per review.
- Start the summary with the count. If there is nothing to fix, say so plainly.

## Recurring rules

<!-- Promote here any comment that shows up in 2+ reviews. One line each, with the date and why. Move it to a lint rule when one can enforce it. -->

- 2026-10-25 — Security counters (rate limiters, lockouts) must check-and-increment atomically, and in-memory maps keyed by request input need a hard size cap: the first login limiter allowed ~200 guesses under concurrency and unbounded growth.
