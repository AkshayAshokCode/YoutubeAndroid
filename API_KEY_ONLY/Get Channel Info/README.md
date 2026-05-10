# Get Channel Info

## What it does

Reads public channel metadata, statistics, branding, and the uploads playlist ID.

## YouTube endpoint

`channels.list`

## Auth type

API key only. No Google Sign-In required.

## Quota cost

1 unit per request

## Files in this folder

- `GetChannelInfo.kt` — actual API implementation and request/response models.
- `GetChannelInfoViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- subscriberCount can be hidden or rounded.
- forUsername only works for legacy usernames; prefer channel ID.
- Use relatedPlaylists.uploads to browse every public upload.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
