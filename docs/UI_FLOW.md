# Android sample app UI flow

This document defines the planned UI for the runnable Android sample app. The top-level `API_KEY_ONLY` and `OAUTH_REQUIRED` folders remain copy-paste examples; the app UI is the guided playground that helps developers discover, run, and understand those examples.

## Core product structure

The app starts with a root selection screen. This is a core product decision, not just a navigation detail.

Users choose between exactly two paths:

1. **API Key APIs**
   - Subtitle: **Public YouTube data without user login**
   - Purpose: allow developers to explore public YouTube Data API endpoints using only an API key.
   - Tone: beginner-friendly, fast, safe, and easy to explore.
   - No Google Sign-In required.

2. **OAuth APIs (Advanced)**
   - Subtitle: **Account-scoped APIs requiring Google Sign-In**
   - Purpose: allow developers to explore advanced YouTube APIs requiring OAuth 2.0.
   - Tone: advanced, developer-focused, explicit about scopes and account permissions.
   - Google Sign-In required before real requests can run.

The UI must strongly communicate the difference between public YouTube APIs and account-scoped APIs requiring OAuth. A developer should understand this distinction before choosing a feature.

## UI goals

- Make the auth boundary obvious on the root selection screen before a developer sees any feature list.
- Make **API Key APIs** feel beginner-friendly after adding `YT_API_KEY`.
- Make **OAuth APIs (Advanced)** feel deliberately advanced and gated behind Google Sign-In and scope consent.
- Show request parameters, loading state, successful response data, raw JSON, quota cost, and common errors on every feature screen.
- Keep every screen aligned with MVVM: UI renders state, ViewModel owns UI logic, repositories/features own API calls.

## Navigation map

```text
MainActivity
└── YouTubeExamplesApp
    ├── RootSelectionScreen
    │   ├── API Key APIs card
    │   │   ├── Subtitle: Public YouTube data without user login
    │   │   ├── Beginner-friendly badge
    │   │   └── API key status
    │   └── OAuth APIs (Advanced) card
    │       ├── Subtitle: Account-scoped APIs requiring Google Sign-In
    │       ├── Advanced badge
    │       └── Google Sign-In status
    ├── ApiKeyApisFeatureListScreen
    │   ├── Search
    │   ├── Video Details
    │   ├── Channel Details
    │   ├── Public Playlists
    │   ├── Trending Videos
    │   └── Categories & Regions
    ├── OAuthApisFeatureListScreen
    │   ├── Upload Videos
    │   ├── Comments
    │   ├── Playlist Management
    │   ├── Subscriptions
    │   ├── Live Streaming
    │   ├── Live Chat
    │   └── Captions
    ├── FeatureDetailScreen(featureId)
    ├── SetupGuideScreen(guideId)
    └── RawResponseScreen(featureId)
```

## Primary user journeys

### 1. First launch

1. User opens the app.
2. `RootSelectionScreen` appears before any feature list.
3. The screen presents two large cards:
   - **API Key APIs** — **Public YouTube data without user login**
   - **OAuth APIs (Advanced)** — **Account-scoped APIs requiring Google Sign-In**
4. The API Key APIs card checks whether `BuildConfig.YOUTUBE_API_KEY` is blank.
5. If blank, show an API key warning and link to `setup-guide/API_KEY_SETUP.md`.
6. The OAuth APIs card shows signed-out state and explains that this path requires Google Sign-In.
7. User can still inspect feature docs and sample responses without making network calls.

### 2. API Key APIs happy path

1. User taps **API Key APIs**.
2. User sees a beginner-friendly list: Search, Video Details, Channel Details, Public Playlists, Trending Videos, Categories & Regions.
3. User chooses **Search**.
4. Feature screen shows:
   - short description
   - quota cost
   - input form
   - endpoint name
   - run button
   - sample response preview
5. User enters a query and taps **Run**.
6. ViewModel emits `Loading`.
7. UI shows progress indicator and disables duplicate run taps.
8. ViewModel emits `Content`.
9. UI shows formatted result cards and a **Raw JSON** tab.
10. If `nextPageToken` exists, UI shows **Load next page**.

### 3. OAuth APIs (Advanced) happy path

1. User taps **OAuth APIs (Advanced)**.
2. User sees an advanced list: Upload Videos, Comments, Playlist Management, Subscriptions, Live Streaming, Live Chat, Captions.
3. If signed out, the feature list is visible but every runnable action shows a lock icon and **Sign in to run** button.
4. User signs in and grants the minimum required scope for the chosen feature.
5. Feature screen enables the request form.
6. User runs the request.
7. UI shows response data, raw JSON, required scopes, and recovery actions for partial failures.

### 4. Error path

Every feature should render errors in the same structure:

```text
Error title
Human-readable explanation
Likely cause
Fix action
Raw API error body toggle
```

Examples:

- Missing API key → link to API key setup guide.
- `quotaExceeded` → show quota cost and explain daily quota reset.
- `401` OAuth expired → show **Refresh sign-in**.
- `403 insufficientPermissions` → show required scope and **Request permission**.
- `404` resource not found → explain private/deleted/wrong ID possibilities.

## Screen contracts

### RootSelectionScreen

Purpose: make the API key vs OAuth product decision impossible to miss.

Content:

- App title and one-line purpose.
- A short explanation: **Public APIs use an API key. Account APIs require Google Sign-In.**
- Primary card 1:
  - Title: **API Key APIs**
  - Subtitle: **Public YouTube data without user login**
  - Badge: **Beginner friendly**
  - Status: API key configured / API key missing
  - Feature preview: Search, Video Details, Channel Details, Public Playlists, Trending Videos, Categories & Regions
- Primary card 2:
  - Title: **OAuth APIs (Advanced)**
  - Subtitle: **Account-scoped APIs requiring Google Sign-In**
  - Badge: **Advanced**
  - Status: Signed in / Not signed in
  - Feature preview: Upload Videos, Comments, Playlist Management, Subscriptions, Live Streaming, Live Chat, Captions
- Links to setup guides.

Actions:

- Open API Key APIs feature list.
- Open OAuth APIs feature list.
- Open API key setup guide.
- Open OAuth setup guide.

### ApiKeyApisFeatureListScreen

Purpose: make public-data APIs feel approachable and safe for beginners.

Header:

- Title: **API Key APIs**
- Subtitle: **Public YouTube data without user login**
- Helper text: **These examples do not require Google Sign-In. Add an API key and run them immediately.**

Feature rows:

- Search
- Video Details
- Channel Details
- Public Playlists
- Trending Videos
- Categories & Regions

Each row/card shows:

- Feature name.
- Endpoint(s).
- Auth type badge: `API key`.
- Quota cost.
- One-line description.
- Completion state: `Runnable` or `Sample only`.

### OAuthApisFeatureListScreen

Purpose: set expectations that these APIs are account-scoped and advanced.

Header:

- Title: **OAuth APIs (Advanced)**
- Subtitle: **Account-scoped APIs requiring Google Sign-In**
- Helper text: **These examples read or modify a signed-in YouTube account and require OAuth scopes.**

Feature rows:

- Upload Videos
- Comments
- Playlist Management
- Subscriptions
- Live Streaming
- Live Chat
- Captions

Each row/card shows:

- Feature name.
- Endpoint(s).
- Auth type badge: `OAuth`.
- Required scope.
- Quota cost.
- One-line description.
- Completion state: `Requires sign-in`, `Runnable`, or `Sample only`.

### FeatureDetailScreen

Purpose: let developers run one feature and understand how to integrate it.

Top section:

- Feature title.
- Auth badge.
- Endpoint(s).
- Quota cost.
- Required OAuth scope, if applicable.
- Link to feature README.

Middle section:

- Request form fields.
- Run button.
- Clear/reset button.
- Use sample response button.

Result section:

- Loading state.
- Error state.
- Parsed result cards.
- Raw JSON tab.
- Copy cURL-like request summary.

Bottom section:

- Edge cases.
- Integration checklist.

### SetupGuideScreen

Purpose: show setup docs in-app for quick troubleshooting.

Guides:

- API key setup.
- OAuth setup.
- SHA-1 fingerprint and key restrictions.

## Feature-specific UI flows

### Search

Inputs:

- Query text.
- Result type: video, channel, playlist.
- Optional page token hidden under advanced options.

Results:

- Thumbnail.
- Title.
- Channel title.
- Description preview.
- ID chip: video/channel/playlist.
- Load next page button.

### Video Details

Inputs:

- One or more video IDs, comma/newline separated.

Results:

- Title.
- Channel.
- Duration.
- View/like/comment counts.
- Privacy/embeddable status.
- Live details if present.

### Channel Details

Inputs:

- One or more channel IDs.

Results:

- Channel title.
- Description.
- Subscriber/video/view counts.
- Uploads playlist ID with shortcut to Browse Playlists.

### Public Playlists

Inputs:

- Channel ID or playlist ID.
- Mode switch: channel playlists or playlist videos.

Results:

- Playlist cards with item count.
- Playlist item cards with position and video ID.
- Load next page button.

### Categories & Regions

Inputs:

- Region code.
- Optional language code.

Results:

- Category list with assignable badge.
- Region list with country code and display name.

### Upload Videos

Inputs:

- Video file picker.
- Title.
- Description.
- Tags.
- Category.
- Privacy: private/unlisted/public.
- Made-for-kids declaration.

Results:

- Upload session URL.
- Progress state.
- Final video ID after upload completion.

### Live Streaming

Inputs:

- Broadcast title.
- Scheduled start time.
- Privacy.
- Stream settings.

Results:

- Broadcast ID.
- Stream ID.
- RTMP ingestion URL.
- Stream key hidden by default with reveal/copy control.
- State transition buttons: testing, live, complete.

### Live Chat

Inputs:

- Active live chat ID or video ID lookup.
- Message text.

Results:

- Message list.
- Polling interval indicator.
- Send message field.
- Disabled/ended chat message state.

### Comments

Inputs:

- Video ID.
- Comment text.

Results:

- Created comment/thread ID.
- Delete action for comments created by the sample.

### Playlist Management

Inputs:

- Playlist title/description/privacy.
- Playlist ID.
- Video ID.

Results:

- Created playlist ID.
- Added playlist item ID.
- Remove playlist item action.

### Subscriptions

Inputs:

- Channel ID.

Results:

- Subscription ID.
- Unsubscribe action.

### Captions

Inputs:

- Video ID.
- Caption file picker.
- Language.
- Caption name.

Results:

- Caption tracks list.
- Upload result.
- Delete action.

## Shared state model

Each feature ViewModel should converge on this state shape:

```kotlin
sealed interface FeatureUiState<out T> {
    data object Idle : FeatureUiState<Nothing>
    data object Loading : FeatureUiState<Nothing>
    data class Content<T>(val data: T, val rawJson: String? = null) : FeatureUiState<T>
    data class Error(
        val title: String,
        val message: String,
        val actionLabel: String? = null,
        val rawBody: String? = null,
    ) : FeatureUiState<Nothing>
}
```

## Recommended implementation phases

### Phase 1 — Compose shell and root selection

- Replace XML `activity_main.xml` usage with a Compose `setContent` host.
- Add Material3 theme.
- Add Compose Navigation.
- Add `RootSelectionScreen` as the first screen.
- Add static **API Key APIs** and **OAuth APIs (Advanced)** feature list screens.
- Make the auth difference visible in labels, subtitles, badges, and helper text.

### Phase 2 — API-key runnable flows

- Implement Search end to end first.
- Add shared request/result components.
- Add remaining API-key screens.
- Add sample response rendering without network calls.

### Phase 3 — OAuth shell

- Add Google Sign-In.
- Add account state card.
- Add required-scope metadata to OAuth feature rows.
- Keep OAuth feature screens in sample-response mode until token handling is complete.

### Phase 4 — OAuth runnable flows

- Implement Manage Playlists and Post Comments first.
- Implement Subscriptions and Captions.
- Implement Upload Video.
- Implement Live Streaming and Live Chat last.

### Phase 5 — polish and tests

- Add ViewModel tests for each screen.
- Add screenshot/golden tests for common states.
- Add fake repositories for offline demo mode.
- Add accessibility labels and TalkBack-friendly result cards.

## Acceptance checklist

A feature screen is complete when it has:

- Auth badge and endpoint label.
- Quota cost visible before running.
- Request form validation.
- Loading state.
- Parsed success state.
- Raw JSON view.
- API error rendering with fix guidance.
- Sample response mode.
- README link or embedded integration notes.
- ViewModel tests for idle, loading, success, and error states.
