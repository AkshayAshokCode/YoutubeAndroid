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

Do not hard-code the key in Kotlin files. This project reads `YT_API_KEY` as a Gradle property, so put it in `~/.gradle/gradle.properties`. The project's `gradle.properties` is committed, and `local.properties` isn't read by `findProperty`.

## Common errors

- `keyInvalid` — wrong key, deleted key, or YouTube Data API v3 is not enabled.
- `quotaExceeded` — project quota is exhausted for the day.
- `accessNotConfigured` — API not enabled in the current Google Cloud project.
- `API_KEY_ANDROID_APP_BLOCKED` / `forbidden` — the key is restricted to Android apps and the request didn't match. Plain REST calls must send `X-Android-Package` and `X-Android-Cert` (SHA-1, no colons); `ApiKeyInterceptor` does this.
