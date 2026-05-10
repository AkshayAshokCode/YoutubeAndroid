# Search Videos

## What it does

Searches public YouTube videos, channels, or playlists by query text using only an API key.

## YouTube endpoint

`search.list`

## Auth type

API key only. No Google Sign-In required.

## Quota cost

100 units per request

## Files in this folder

- `SearchVideos.kt` — actual API implementation and request/response models.
- `SearchVideosViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- Search is expensive compared with simple reads; debounce text input.
- The API returns IDs in different fields depending on result type.
- Use pageToken for pagination instead of increasing maxResults beyond 50.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
