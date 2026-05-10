# Browse Playlists

## What it does

Lists public playlists for a channel and reads videos inside a selected playlist.

## YouTube endpoint

`playlists.list + playlistItems.list`

## Auth type

API key only. No Google Sign-In required.

## Quota cost

1 unit for playlists.list, 1 unit for playlistItems.list

## Files in this folder

- `BrowsePlaylists.kt` — actual API implementation and request/response models.
- `BrowsePlaylistsViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- Playlist item order is controlled by the playlist owner.
- Deleted/private videos can appear with limited metadata.
- Use nextPageToken to walk long playlists.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
