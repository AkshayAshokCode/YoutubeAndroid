# API key setup

This is the fast path for `API_KEY_ONLY` examples.

## 30-second setup

1. Open Google Cloud Console.
2. Create or select a project.
3. Enable **YouTube Data API v3**.
4. Create an **API key** credential.
5. Add it to Gradle:

```properties
YT_API_KEY=your_api_key_here
```

The Android app module already exposes this value as `BuildConfig.YOUTUBE_API_KEY`.

## Recommended Android usage

Do not hard-code the key in Kotlin files. Read it from `BuildConfig`, `local.properties`, Gradle properties, or your preferred secrets setup.

## Common errors

- `keyInvalid` — wrong key, deleted key, or YouTube Data API v3 is not enabled.
- `quotaExceeded` — project quota is exhausted for the day.
- `accessNotConfigured` — API not enabled in the current Google Cloud project.
- `forbidden` — key restrictions do not match the calling Android app.
