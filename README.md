# YoutubeAndroid

A Kotlin Android reference project for developers who want to integrate the YouTube Data API v3 without starting from raw documentation.

The project is intentionally split into two learning paths:

1. **API_KEY_ONLY** — public data features that work with a YouTube Data API key and do not require user login.
2. **OAUTH_REQUIRED** — account-scoped features that require Google Sign-In / OAuth 2.0 user consent.

## Project goals

- Demonstrate every major YouTube Data API v3 feature in a practical Android app.
- Keep every feature self-contained with its own repository, ViewModel/UI state where applicable, and README.
- Use Kotlin, MVVM, Repository pattern, Coroutines, Flow, Retrofit, Moshi, OkHttp, and Hilt.
- Provide clean error handling and comments for the API-specific details that usually slow Android developers down.

## Current implementation status

### API_KEY_ONLY

- ✅ Search videos, channels, and playlists
- ✅ Fetch video details and metadata
- ✅ Get channel info and stats
- ✅ Browse public playlists and playlist items
- ✅ Get video categories and supported regions

### OAUTH_REQUIRED

- ⬜ Upload videos
- ⬜ Live streaming: create, manage, and transition broadcast states
- ⬜ Live chat: read and post messages
- ⬜ Post and delete comments
- ⬜ Manage playlists: create playlists, add videos, remove videos
- ⬜ Subscribe and unsubscribe to channels
- ⬜ Manage captions

## API key setup

Add your YouTube Data API key to `~/.gradle/gradle.properties` or the project `gradle.properties` file:

```properties
YT_API_KEY=your_api_key_here
```

The app exposes this value as `BuildConfig.YOUTUBE_API_KEY` and appends it to API-key-only requests through `ApiKeyInterceptor`.

## Package map

```text
app/src/main/java/com/akshayashokcode/youtubeandroid/
├── core/
│   ├── model/          # Shared YouTube response DTOs
│   ├── network/        # Retrofit API and API-key interceptor
│   ├── result/         # Result wrapper for repositories
│   └── ui/             # Shared UI state model
├── di/                 # Hilt modules
└── features/
    ├── apikeyonly/     # Public API-key-only examples
    └── oauthrequired/  # OAuth feature guides and future implementations
```

## Feature folders

Each feature folder contains the code and README needed to understand that feature in isolation. Start with `features/apikeyonly/search` for the smallest end-to-end MVVM example.
