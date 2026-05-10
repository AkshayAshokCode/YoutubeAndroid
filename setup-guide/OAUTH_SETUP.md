# OAuth setup for Android

Use this guide for folders under `OAUTH_REQUIRED`.

## Checklist

1. Enable **YouTube Data API v3** in Google Cloud Console.
2. Configure the OAuth consent screen.
3. Create an Android OAuth client ID.
4. Add your app package name.
5. Add your debug and release SHA-1 fingerprints.
6. Add Google Sign-In to your Android app.
7. Request only the scopes needed by the feature.
8. Pass a fresh access token into the feature implementation.

## Typical scopes

| Feature | Example scope |
|---|---|
| Upload Video | `https://www.googleapis.com/auth/youtube.upload` |
| Live Streaming | `https://www.googleapis.com/auth/youtube` |
| Live Chat | `https://www.googleapis.com/auth/youtube` |
| Post Comments | `https://www.googleapis.com/auth/youtube.force-ssl` |
| Manage Playlists | `https://www.googleapis.com/auth/youtube` |
| Subscribe to Channels | `https://www.googleapis.com/auth/youtube` |
| Manage Captions | `https://www.googleapis.com/auth/youtube.force-ssl` |

## Token provider pattern

Each OAuth example expects this style of dependency:

```kotlin
val accessTokenProvider: suspend () -> String = {
    // Return a fresh OAuth access token for the signed-in Google account.
    currentGoogleAccountAccessToken()
}
```

If the API returns 401 or 403, refresh sign-in state, check the requested scope, and confirm the user has permission on the target channel/resource.
