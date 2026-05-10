# Android sample app UI flow

This document defines the planned UI for the runnable Android sample app. The top-level `API_KEY_ONLY` and `OAUTH_REQUIRED` folders remain copy-paste examples; the app UI is the guided playground that helps developers discover, run, and understand those examples.

## UI goals

- Make the auth boundary obvious before a developer taps a feature.
- Let developers run API-key-only examples quickly after adding `YT_API_KEY`.
- Gate OAuth-required examples behind Google Sign-In and scope consent.
- Show request parameters, loading state, successful response data, raw JSON, quota cost, and common errors on every feature screen.
- Keep every screen aligned with MVVM: UI renders state, ViewModel owns UI logic, repositories/features own API calls.

## Navigation map

```text
MainActivity
└── YouTubeExamplesApp
    ├── HomeScreen
    │   ├── API key status card
    │   ├── OAuth sign-in status card
    │   ├── API_KEY_ONLY section card
    │   └── OAUTH_REQUIRED section card
    ├── ApiKeyOnlyFeatureListScreen
    │   ├── Search Videos
    │   ├── Get Video Details
    │   ├── Get Channel Info
    │   ├── Browse Playlists
    │   └── Get Categories and Regions
    ├── OAuthRequiredFeatureListScreen
    │   ├── Upload Video
    │   ├── Live Streaming
    │   ├── Live Chat
    │   ├── Post Comments
    │   ├── Manage Playlists
    │   ├── Subscribe to Channels
    │   └── Manage Captions
    ├── FeatureDetailScreen(featureId)
    ├── SetupGuideScreen(guideId)
    └── RawResponseScreen(featureId)
```

## Primary user journeys

### 1. First launch

1. User opens the app.
2. `HomeScreen` checks whether `BuildConfig.YOUTUBE_API_KEY` is blank.
3. If blank, show an API key warning card with a link to `setup-guide/API_KEY_SETUP.md`.
4. OAuth card shows signed-out state and explains that OAuth is only needed for account features.
5. User can still open feature detail screens and inspect sample responses without making network calls.

### 2. API-key-only happy path

1. User taps **API_KEY_ONLY**.
2. User chooses **Search Videos**.
3. Feature screen shows:
   - short description
   - quota cost
   - input form
   - endpoint name
   - run button
   - sample response preview
4. User enters a query and taps **Run**.
5. ViewModel emits `Loading`.
6. UI shows progress indicator and disables duplicate run taps.
7. ViewModel emits `Content`.
8. UI shows formatted result cards and a **Raw JSON** tab.
9. If `nextPageToken` exists, UI shows **Load next page**.

### 3. OAuth-required happy path

1. User taps **OAUTH_REQUIRED**.
2. If signed out, feature list is visible but each feature shows a lock icon and **Sign in to run** button.
3. User signs in and grants the minimum required scope for the chosen feature.
4. Feature screen enables the request form.
5. User runs the request.
6. UI shows response data, raw JSON, and recovery actions for partial failures.

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

### HomeScreen

Purpose: explain the repo and route developers to the correct auth section.

Content:

- App title and one-line purpose.
- API key status card.
- OAuth status card.
- Two primary cards:
  - **API_KEY_ONLY: public read-only examples**
  - **OAUTH_REQUIRED: signed-in account examples**
- Links to setup guides.

Actions:

- Open API-key feature list.
- Open OAuth feature list.
- Open setup guide.

### FeatureListScreen

Purpose: make features scannable and show auth/quota before opening a feature.

Each row/card shows:

- Feature name.
- Endpoint(s).
- Auth type badge: `API key` or `OAuth`.
- Quota cost.
- One-line description.
- Completion state: `Runnable`, `Sample only`, or `Requires sign-in`.

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

### Search Videos

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

### Get Video Details

Inputs:

- One or more video IDs, comma/newline separated.

Results:

- Title.
- Channel.
- Duration.
- View/like/comment counts.
- Privacy/embeddable status.
- Live details if present.

### Get Channel Info

Inputs:

- One or more channel IDs.

Results:

- Channel title.
- Description.
- Subscriber/video/view counts.
- Uploads playlist ID with shortcut to Browse Playlists.

### Browse Playlists

Inputs:

- Channel ID or playlist ID.
- Mode switch: channel playlists or playlist videos.

Results:

- Playlist cards with item count.
- Playlist item cards with position and video ID.
- Load next page button.

### Get Categories and Regions

Inputs:

- Region code.
- Optional language code.

Results:

- Category list with assignable badge.
- Region list with country code and display name.

### Upload Video

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

### Post Comments

Inputs:

- Video ID.
- Comment text.

Results:

- Created comment/thread ID.
- Delete action for comments created by the sample.

### Manage Playlists

Inputs:

- Playlist title/description/privacy.
- Playlist ID.
- Video ID.

Results:

- Created playlist ID.
- Added playlist item ID.
- Remove playlist item action.

### Subscribe to Channels

Inputs:

- Channel ID.

Results:

- Subscription ID.
- Unsubscribe action.

### Manage Captions

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

### Phase 1 — Compose shell

- Replace XML `activity_main.xml` usage with a Compose `setContent` host.
- Add Material3 theme.
- Add Compose Navigation.
- Add `HomeScreen` and static feature list screens.

### Phase 2 — API-key runnable flows

- Implement Search Videos end to end first.
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
