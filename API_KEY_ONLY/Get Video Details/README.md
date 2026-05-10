# Get Video Details

## What it does

Fetches metadata, duration, statistics, privacy/public status, and live streaming details for known video IDs.

## YouTube endpoint

`videos.list`

## Auth type

API key only. No Google Sign-In required.

## Quota cost

1 unit per request

## Files in this folder

- `GetVideoDetails.kt` — actual API implementation and request/response models.
- `GetVideoDetailsViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- Pass up to 50 comma-separated video IDs per request.
- Some statistics can be hidden by the owner.
- Live fields only appear for live or scheduled-live videos.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
