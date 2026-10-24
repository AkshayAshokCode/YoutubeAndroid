# Project status

What's done and what's left, measured against the plan in [UI_FLOW.md](UI_FLOW.md).

_Last reviewed: 2026-10-24. `./gradlew assembleDebug testDebugUnitTest` passes (24 unit tests, 0 failures)._

## Summary

| Area | State |
|---|---|
| API-key features (6 screens) | ✅ Runnable end to end |
| OAuth features (7 screens) | 🟡 UI, ViewModels, and API calls wired; **sign-in is a stub**, so requests fail with 401 |
| Navigation | ✅ Compose `NavHost`, 17 routes |
| Unit tests | 🟡 7 of 13 feature ViewModels covered |
| Planned polish (raw JSON, setup guide screen, structured errors) | ⬜ Not started |
| Docs | 🟡 Root README status section was outdated (now links here) |

---

## Done

### Phase 1 — Compose shell
- [x] Compose `setContent` host in `MainActivity`, Material3
- [x] Root selection screen with API key status card and OAuth status card
- [x] Feature list screens with endpoint, quota cost, description, and status badge

### Phase 2 — API-key runnable flows
All live under `features/apikeyonly/`, each with Repository + ViewModel + Screen.

- [x] **Search** — videos / channels / playlists, pagination (`search/`)
- [x] **Video Details** — snippet, stats, status, live details (`video_details/`)
- [x] **Channel Info** — stats, uploads playlist, "Browse uploads" shortcut (`channel_info/`)
- [x] **Browse Playlists** — channel playlists + playlist items modes, pagination, `?playlistId` deep link (`public_playlists/`)
- [x] **Categories & Regions** (`categories_regions/`)
- [x] **Trending Videos** — `videos.list chart=mostPopular`, reuses `VideoDetailsRepository` (`trending_videos/`), added beyond the original plan

### Phase 3 — OAuth shell
- [x] `AuthState` / `AuthRepository` / `AuthViewModel` (`core/auth/`)
- [x] `OAuthInterceptor` adds `Authorization: Bearer <token>`
- [x] Separate OkHttp/Retrofit instances via `@ApiKeyClient` and `@OAuthClient` qualifiers (`di/`)
- [x] `YouTubeOAuthApiService` with all write and OAuth-only read endpoints
- [x] Auth card on root screen; lock overlay on OAuth feature list when signed out
- [ ] **Real Google Sign-In.** See [Remaining → 1](#1-real-google-sign-in-blocker)

### Phase 4 — OAuth runnable flows
All live under `features/oauthrequired/`, each with Repository + ViewModel + Screen.

- [x] **Post Comments** — insert thread, delete (`comments/`)
- [x] **Manage Playlists** — create playlist, add video, remove item (`manage_playlists/`)
- [x] **Subscriptions** — subscribe, unsubscribe (`subscriptions/`)
- [x] **Manage Captions** — list, upload via file picker, delete (`captions/`)
- [x] **Upload Video** — single-request multipart upload (`upload_video/`)
- [x] **Live Streaming** — create broadcast + stream, bind, testing/live/complete transitions, stream key reveal (`live_streaming/`)
- [x] **Live Chat** — polling driven by `pollingIntervalMillis`, send messages (`live_chat/`)

### Phase 5 — Tests (partial)
ViewModel unit tests (JUnit + MockK + Turbine + coroutines-test):
- [x] SearchViewModel
- [x] VideoDetailsViewModel
- [x] ChannelInfoViewModel
- [x] CategoriesRegionsViewModel
- [x] TrendingVideosViewModel
- [x] CommentsViewModel
- [x] SubscriptionsViewModel

### Supporting material
- [x] Top-level copy-paste example folders: `API_KEY_ONLY/` and `OAUTH_REQUIRED/`, each with code, ViewModel, README, `example-response.json`
- [x] Setup guides: `setup-guide/API_KEY_SETUP.md`, `OAUTH_SETUP.md`, `SHA1_FINGERPRINT.md`
- [x] Per-feature READMEs inside `features/`

---

## Remaining

Ordered by priority.

### 0. Housekeeping (do first)
- [ ] **Move `YT_API_KEY` out of the project `gradle.properties`.** That file is tracked by git, so the key would be committed. Put it in `~/.gradle/gradle.properties` or `local.properties` (both git-ignored) instead.
- [ ] **Commit the current work.** Most of Phases 2–5 is still untracked or uncommitted.

### 1. Real Google Sign-In (blocker)
`core/auth/AuthRepository.kt` currently fakes sign-in: it waits 600 ms and sets `demo@example.com` with token `"REPLACE_WITH_REAL_OAUTH_TOKEN"`. Every OAuth feature returns 401 until this is replaced.

- [ ] Add an auth dependency to `libs.versions.toml` (Credential Manager + `play-services-auth` `AuthorizationClient`, or AppAuth). None is present today.
- [ ] Create OAuth client IDs in Google Cloud (Android client with debug/release SHA-1, see `setup-guide/OAUTH_SETUP.md`)
- [ ] Request per-feature scopes (`youtube`, `youtube.upload`, `youtube.force-ssl`). The scope table is in `OAUTH_SETUP.md`.
- [ ] Get a real **access token**. The stub's comment suggests `serverAuthCode`, but that is an auth code for a backend, not a bearer token.
- [ ] Handle token expiry/refresh: on 401, re-authorize and retry
- [ ] Handle `403 insufficientPermissions` by requesting the missing scope

### 2. Feature gaps
- [ ] **Resumable upload** for Upload Video. Today it is a single multipart request, which fails for large files. The feature README already promises resumable uploads, progress, and an upload session URL.
- [ ] **Captions update.** The OAuth feature card says "List, upload, update, and delete", but there's no `captions.update` endpoint or UI.
- [ ] Live Chat: look up `liveChatId` from a video ID (UI_FLOW lists this input)

### 3. Planned UI polish from UI_FLOW.md
- [ ] **Raw JSON** tab on each feature screen
- [ ] **Sample-response mode**: render `example-response.json` without spending quota
- [ ] **SetupGuideScreen**: in-app setup docs
- [ ] **Structured errors**: title / explanation / likely cause / fix action / raw body. `UiState.Error` holds only a `message` today; UI_FLOW proposes a richer `FeatureUiState`.
- [ ] Specific handling for `quotaExceeded`, 401 (refresh sign-in), 403 (request scope), 404
- [ ] Per-screen header with endpoint, quota cost, required scope, README link (quota currently shows only on list cards)

### 4. Tests
- [ ] ViewModel tests: PublicPlaylists, ManagePlaylists, Captions, VideoUpload, LiveStreaming, LiveChat, Auth
- [ ] Repository tests (e.g. MockWebServer against Retrofit services)
- [ ] Compose UI / instrumented tests (`androidTest` only has the template test)

### 5. Cleanup and docs
- [ ] Merge `features/oauthrequired/upload_videos/README.md` into `upload_video/`. The README-only folder is left over.
- [ ] Check that the top-level `API_KEY_ONLY/` and `OAUTH_REQUIRED/` copy-paste examples still match the app code
- [ ] Update root `README.md`: Trending Videos, auth architecture, how to run tests
