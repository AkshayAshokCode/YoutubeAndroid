# Upload video

OAuth-required. Uploads a video with title, description, tags, category, privacy status, and the made-for-kids declaration.

- **Endpoint:** `videos.insert` with the [resumable upload protocol](https://developers.google.com/youtube/v3/guides/using_resumable_upload_protocol)
- **Scope:** `youtube.upload` is the narrowest scope that works (this app requests `youtube.force-ssl`, which also covers it)
- **Quota:** 1600 units per upload (default daily quota is 10,000)

## How it works

| File | Role |
|---|---|
| `UploadSource.kt` | Reads byte ranges straight from the picked `content://` Uri. Nothing is copied to cache or loaded whole. |
| `VideoUploadRepository.kt` | Runs the protocol and emits `UploadEvent.Progress` / `UploadEvent.Completed`. |
| `VideoUploadViewModel.kt` | Turns events into `uiState` plus a `progress` fraction. |
| `ui/UploadVideoScreen.kt` | File picker, metadata form, progress bar. |

1. `POST upload/youtube/v3/videos?uploadType=resumable` with the metadata as JSON, plus
   `X-Upload-Content-Length` and `X-Upload-Content-Type`. The upload session URI comes back in `Location`.
2. `PUT` the file to the session URI in 8 MiB chunks with `Content-Range: bytes start-end/total`.
   The server replies `308 Resume Incomplete` with a `Range` header after each chunk, and `200`/`201` with the `Video` at the end.
3. On a dropped connection or `5xx`, back off (1 s, 2 s, 4 s, 8 s, 16 s), send an empty `PUT` with
   `Content-Range: bytes */total` to ask how many bytes the server has, and continue from there.
   A `404`/`410` means the session expired; start again.

## Gotchas

- Videos uploaded from an API project that Google hasn't audited are **locked to private**, whatever privacy you choose.
- Chunk sizes must be multiples of 256 KiB (except the last chunk).
- Not included: custom thumbnails (`thumbnails.set`), resuming after the app is killed (persist the session URI and offset to do that).
