# OAUTH_REQUIRED features

These examples require Google Sign-In / OAuth 2.0 because they read or mutate a user's YouTube account. API keys are not enough for these operations.

## Planned folders

- `upload_videos` — resumable uploads, privacy status, title, description, tags, and thumbnails.
- `live_streaming` — broadcast creation, stream binding, health checks, and lifecycle transitions.
- `live_chat` — read live chat messages, post messages, and handle polling intervals.
- `comments` — insert, moderate, and delete comment threads/comments.
- `manage_playlists` — create playlists and add/remove playlist items.
- `subscriptions` — subscribe and unsubscribe from channels.
- `captions` — list, upload, download, update, and delete caption tracks.

## OAuth implementation plan

1. Add Google Sign-In and request the smallest scopes needed by each feature.
2. Store account/session state outside feature repositories.
3. Inject an authenticated HTTP client that adds `Authorization: Bearer <access_token>`.
4. Keep account-specific samples separate from API-key-only samples to avoid confusing quota, auth, and consent requirements.
