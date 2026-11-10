# YoutubeAndroid

A Kotlin Android reference app for the **YouTube Data API v3**. Every major API feature is a small, self-contained, runnable example, so you can see how it works on a device and copy it into your own app instead of starting from the raw API docs.

Features are split by how they authenticate:

1. **API key only.** Public data, no user login.
2. **OAuth required.** Features that read or change the signed-in user's YouTube account.

Built with Kotlin, Jetpack Compose, MVVM, Coroutines/Flow, Hilt, Retrofit, OkHttp and Moshi.

## Features

### API key only

| Feature | Endpoint(s) | Quota |
|---|---|---|
| Search videos, channels, playlists | `search.list` | 100 |
| Video details | `videos.list` | 1 |
| Channel info, with a shortcut to the uploads playlist | `channels.list` | 1 |
| Browse public playlists and their videos | `playlists.list`, `playlistItems.list` | 1 per request |
| Trending videos by region and category | `videos.list?chart=mostPopular` | 1 |
| Video categories and supported regions | `videoCategories.list`, `i18nRegions.list` | 1 per request |

### OAuth required

| Feature | Endpoint(s) | Quota |
|---|---|---|
| Upload video: resumable, chunked, with progress and automatic resume | `videos.insert` | 1600 |
| Post and delete comments | `commentThreads.insert`, `comments.delete` | 50 per write |
| Manage playlists: create, add and remove videos | `playlists.insert`, `playlistItems.*` | 50 per write |
| Subscribe and unsubscribe | `subscriptions.insert`, `subscriptions.delete` | 50 per write |
| Manage captions: list, upload, delete | `captions.*` | 50+ |
| Live streaming: broadcast, stream, bind, testing → live → complete | `liveBroadcasts.*`, `liveStreams.*` | 50+ |
| Live chat: poll messages, send messages | `liveChatMessages.*` | 1+ |

The default quota is 10,000 units per day per Google Cloud project.

**Status:** all features are implemented. Sign-in, the API-key features and Subscriptions have been verified on a device; the other OAuth features haven't been run against the real API yet. See [docs/PROJECT_STATUS.md](docs/PROJECT_STATUS.md) for the done/remaining checklist.

## Getting started

### 1. API key (needed for every feature)

1. In [Google Cloud Console](https://console.cloud.google.com/), create a project and enable **YouTube Data API v3**.
2. Create an **API key** credential.
3. Add it to `~/.gradle/gradle.properties`, which lives outside the repo:

   ```properties
   YT_API_KEY=your_api_key_here
   ```

   Don't put it in the project's `gradle.properties`; that file is committed.
4. Recommended: restrict the key to **Android apps** (package `com.akshayashokcode.youtubeandroid` + your SHA-1) and to **YouTube Data API v3**. `ApiKeyInterceptor` sends the `X-Android-Package` and `X-Android-Cert` headers that a restricted key requires.

Details: [setup-guide/API_KEY_SETUP.md](setup-guide/API_KEY_SETUP.md).

### 2. OAuth (only for the OAuth features)

1. **Google Auth Platform → Audience:** set the user type to External and add your Google account under **Test users**.
2. **Data Access:** add the scope `.../auth/youtube.force-ssl`.
3. **Clients:** create an **Android** client with package name `com.akshayashokcode.youtubeandroid` and your debug SHA-1. Get the SHA-1 from `./gradlew signingReport`.

No client ID goes into the code: Google matches the app by package name and SHA-1. Details: [setup-guide/OAUTH_SETUP.md](setup-guide/OAUTH_SETUP.md) and [setup-guide/SHA1_FINGERPRINT.md](setup-guide/SHA1_FINGERPRINT.md).

### 3. Run

Open the project in Android Studio and run the `app` configuration on a device or emulator with **Google Play** services. Or from the command line:

```bash
./gradlew installDebug
```

## Things that will trip you up

- **The account needs a YouTube channel.** A Google account without one can sign in and subscribe, but creating playlists, comments, uploads, captions or broadcasts fails with `401 youtubeSignupRequired`. Create a channel at youtube.com first.
- **Live streaming must be enabled** for the channel in YouTube Studio (Create → Go live), or the API returns `403 liveStreamingNotEnabled`. The first activation can take up to 24 hours.
- **Unverified apps:** until the OAuth consent screen is verified, only listed test users can sign in (others get `403 access_denied`), and the consent screen shows an "unverified app" warning.
- **Uploads stay private:** videos uploaded from an API project Google hasn't audited are locked to private, whatever privacy you choose.
- **Error messages:** when a call fails, the app shows Google's message and reason code, e.g. `The user is not enabled for live streaming. (HTTP 403, liveStreamingNotEnabled)`. Search the reason code in the [YouTube Data API errors reference](https://developers.google.com/youtube/v3/docs/errors).

## How it works

### Authentication

| Piece | What it does |
|---|---|
| `ApiKeyInterceptor` | Adds `key=` and the Android app headers to API-key requests. |
| `AuthRepository` | Gets OAuth access tokens with Google Identity Services' `AuthorizationClient`, restores the session silently on launch, and revokes access on sign-out. |
| `OAuthInterceptor` | Adds `Authorization: Bearer <token>` to OAuth requests. |
| `OAuthTokenAuthenticator` | On a `401`, gets a fresh token silently and retries the request once. Access tokens last about an hour. |
| `NetworkModule` | Two separate OkHttp/Retrofit stacks, `@ApiKeyClient` and `@OAuthClient`, so tokens never go to API-key calls. |

### Package map

```text
app/src/main/java/com/akshayashokcode/youtubeandroid/
├── core/
│   ├── auth/           # AuthRepository, AuthViewModel, AuthState
│   ├── model/          # Moshi DTOs for YouTube requests and responses
│   ├── network/        # Retrofit services, interceptors, token authenticator
│   ├── result/         # AppResult wrapper + readable API error messages
│   └── ui/             # Shared UiState
├── di/                 # Hilt modules and qualifiers
├── ui/                 # YouTubeExamplesApp: Compose NavHost and home screens
└── features/
    ├── apikeyonly/     # search, video_details, channel_info, public_playlists,
    │                   # trending_videos, categories_regions
    └── oauthrequired/  # upload_video, comments, manage_playlists, subscriptions,
                        # captions, live_streaming, live_chat
```

Each feature folder follows the same pattern: `XRepository` makes the API calls and returns `Flow<AppResult<T>>`, `XViewModel` exposes `StateFlow` UI state, and `ui/XScreen.kt` renders it. Most have a README with endpoint notes. Start with `features/apikeyonly/search` for the smallest end-to-end example, and see `features/oauthrequired/upload_video` for the resumable upload protocol.

The top-level [`API_KEY_ONLY/`](API_KEY_ONLY) and [`OAUTH_REQUIRED/`](OAUTH_REQUIRED) folders hold standalone copy-paste versions of each feature with sample JSON responses. They may lag behind the app code.

## Tests

```bash
./gradlew testDebugUnitTest
```

Unit tests cover the feature ViewModels, `AuthViewModel`, the resumable upload protocol (`VideoUploadRepositoryTest`) and API error parsing. They use JUnit 4, MockK, Turbine and kotlinx-coroutines-test.

## Docs

- [docs/PROJECT_STATUS.md](docs/PROJECT_STATUS.md): what's done and what's left
- [docs/UI_FLOW.md](docs/UI_FLOW.md): the planned app UI and screen contracts
- [setup-guide/](setup-guide): API key, OAuth and SHA-1 setup
