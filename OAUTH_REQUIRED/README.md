# OAUTH_REQUIRED

Use these folders when the YouTube Data API v3 request needs the signed-in user's YouTube account. API keys are not enough for these operations.

## Features

- `Upload Video` — resumable upload metadata handshake.
- `Live Streaming` — create broadcasts, create streams, bind, and transition states.
- `Live Chat` — poll and post live chat messages.
- `Post Comments` — post top-level comments and delete comments.
- `Manage Playlists` — create playlists and add/remove playlist items.
- `Subscribe to Channels` — subscribe and unsubscribe the authenticated user.
- `Manage Captions` — list, upload, and delete caption tracks.

## How to use a folder

1. Complete `setup-guide/OAUTH_SETUP.md`.
2. Request the smallest scope required by the folder.
3. Provide `accessTokenProvider: suspend () -> String` to the implementation.
4. Handle 401/403 by refreshing sign-in state or requesting missing consent.
