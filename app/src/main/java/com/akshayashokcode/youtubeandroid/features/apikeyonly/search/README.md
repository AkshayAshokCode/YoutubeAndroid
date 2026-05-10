# Search videos, channels, and playlists

Demonstrates `search.list` for public YouTube search.

## Supports

- Free-text search with `q`.
- Optional result type filtering: `video`, `channel`, or `playlist`.
- Optional channel-scoped search with `channelId`.
- Pagination with `pageToken`.

## Android pattern

- `SearchRepository` wraps the Retrofit call and exposes `Flow<AppResult<SearchListResponse>>`.
- `SearchViewModel` validates input and maps repository results into `UiState`.
