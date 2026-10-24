# Project status

What's done and what's left, measured against the plan in [UI_FLOW.md](UI_FLOW.md).

_Last reviewed: 2026-10-24. `./gradlew assembleDebug testDebugUnitTest` passes (30 unit tests, 0 failures)._

## Summary

| Area | State |
|---|---|
| API-key features (6 screens) | ✅ Runnable end to end |
| OAuth features (7 screens) | 🟡 Wired to real Google sign-in; **not yet tested end to end** with a registered OAuth client |
| Navigation | ✅ Compose `NavHost`, 17 routes |
| Unit tests | 🟡 7 of 13 feature ViewModels + AuthViewModel covered |
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
- [x] Real Google sign-in via `AuthorizationClient`, silent token refresh on 401, session restore on launch

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
- [x] AuthViewModel

### Supporting material
- [x] Top-level copy-paste example folders: `API_KEY_ONLY/` and `OAUTH_REQUIRED/`, each with code, ViewModel, README, `example-response.json`
- [x] Setup guides: `setup-guide/API_KEY_SETUP.md`, `OAUTH_SETUP.md`, `SHA1_FINGERPRINT.md`
- [x] Per-feature READMEs inside `features/`

---

## Remaining

Ordered by priority.

### 0. Housekeeping
- [x] Keep `YT_API_KEY` in `~/.gradle/gradle.properties`, not the tracked project `gradle.properties`
- [x] Commit Phases 2–5 work (branch `feature/oauth-features-and-status`)
- [x] Merged into `master` (PR #10)

### 1. Verify OAuth end to end (needs your Google Cloud setup)
Code is done (`AuthRepository`, `OAuthTokenAuthenticator`, consent launcher in `YouTubeExamplesApp`). What's left needs a Cloud project:

- [ ] Register an **Android** OAuth client for `com.akshayashokcode.youtubeandroid` with the debug SHA-1 (`./gradlew signingReport`), and add your account as a test user. See `setup-guide/OAUTH_SETUP.md`.
- [ ] Run each OAuth feature on a device against a test channel; fix whatever the real API rejects
- [ ] Add the release SHA-1 before shipping a release build

### 2. Feature gaps
- [ ] **Resumable upload** for Upload Video. Today it is a single multipart request, which fails for large files. The feature README already promises resumable uploads, progress, and an upload session URL.
- [ ] **Captions update.** The OAuth feature card says "List, upload, update, and delete", but there's no `captions.update` endpoint or UI.
- [ ] Live Chat: look up `liveChatId` from a video ID (UI_FLOW lists this input)

### 3. Planned UI polish from UI_FLOW.md
- [ ] **Raw JSON** tab on each feature screen
- [ ] **Sample-response mode**: render `example-response.json` without spending quota
- [ ] **SetupGuideScreen**: in-app setup docs
- [ ] **Structured errors**: title / explanation / likely cause / fix action / raw body. `UiState.Error` holds only a `message` today; UI_FLOW proposes a richer `FeatureUiState`.
- [ ] Specific handling for `quotaExceeded`, 403, and 404 (401 is now retried with a fresh token automatically)
- [ ] Per-screen header with endpoint, quota cost, required scope, README link (quota currently shows only on list cards)

### 4. Tests
- [ ] ViewModel tests: PublicPlaylists, ManagePlaylists, Captions, VideoUpload, LiveStreaming, LiveChat
- [ ] Repository tests (e.g. MockWebServer against Retrofit services)
- [ ] Compose UI / instrumented tests (`androidTest` only has the template test)

### 5. Cleanup and docs
- [ ] Merge `features/oauthrequired/upload_videos/README.md` into `upload_video/`. The README-only folder is left over.
- [ ] Check that the top-level `API_KEY_ONLY/` and `OAUTH_REQUIRED/` copy-paste examples still match the app code
- [ ] Update root `README.md`: Trending Videos, auth architecture, how to run tests
