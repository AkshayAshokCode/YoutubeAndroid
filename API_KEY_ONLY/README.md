# API_KEY_ONLY

Use these folders when the YouTube Data API v3 request only reads public data and can be authorized with a simple API key.

## Features

- `Search Videos` — search public videos, channels, or playlists.
- `Get Video Details` — fetch snippet, duration, statistics, status, and live details for video IDs.
- `Get Channel Info` — fetch public channel metadata, stats, branding, and uploads playlist IDs.
- `Browse Playlists` — list public channel playlists and playlist videos.
- `Get Categories and Regions` — load supported regions and assignable video categories.

## How to use a folder

1. Copy the folder into your Android app.
2. Add the `Api` interface to your Retrofit service setup.
3. Pass your API key into the feature implementation.
4. Render the ViewModel state in your UI.
5. Use `example-response.json` to build and test UI states without spending quota.
