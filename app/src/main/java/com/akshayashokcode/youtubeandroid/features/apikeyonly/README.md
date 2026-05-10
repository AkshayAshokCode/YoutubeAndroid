# API_KEY_ONLY features

These examples call public YouTube Data API v3 endpoints with a server-issued API key. They do not require Google Sign-In and should be the first section developers explore.

## Implemented folders

- `search` — searches videos, channels, and playlists with `search.list`.
- `video_details` — fetches metadata, statistics, status, and live details with `videos.list`.
- `channel_info` — fetches channel metadata, branding, upload playlist IDs, and stats with `channels.list`.
- `public_playlists` — reads public playlists and playlist items with `playlists.list` and `playlistItems.list`.
- `categories_regions` — reads video categories and supported i18n regions.

## Common flow

1. Repository calls `YouTubeApiService`.
2. Retrofit suspending function executes on the caller coroutine.
3. Repository emits `AppResult` through Flow.
4. ViewModel maps `AppResult` into `UiState` for UI rendering.
