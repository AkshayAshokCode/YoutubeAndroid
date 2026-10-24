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

## How this app gets tokens

The runnable app uses Google Identity Services' `AuthorizationClient` (`play-services-auth`), in
`core/auth/AuthRepository.kt`:

- **No client ID in code.** Google matches the app to the *Android* OAuth client by package name
  (`com.akshayashokcode.youtubeandroid`) and SHA-1. If they don't match, sign-in fails with
  `DEVELOPER_ERROR` (status 10) and the app shows "OAuth client not configured".
- **One scope:** `youtube.force-ssl` (plus `email` and `profile` for the account card). It authorizes
  every endpoint in this app. Your own app should request only the scopes it needs, see the table above.
- **Consent UI:** when Google needs the account picker or consent screen, `AuthRepository.signIn()`
  returns a `PendingIntent`, which the Compose UI launches with `StartIntentSenderForResult`.
- **Expiry:** access tokens last about an hour. `OAuthTokenAuthenticator` catches a 401, clears the
  cached token, gets a fresh one silently, and retries the request once.
- **Restart:** the app remembers that you signed in and restores the session silently on launch.
- **Sign out** revokes access, so the next sign-in shows the account picker again.

### Testing with an unverified app

While the OAuth consent screen is in *Testing* mode, only accounts listed under **Test users** can
sign in. Add your Google account there, and expect an "unverified app" warning on the consent screen.

## Token provider pattern

Each OAuth example expects this style of dependency:

```kotlin
val accessTokenProvider: suspend () -> String = {
    // Return a fresh OAuth access token for the signed-in Google account.
    currentGoogleAccountAccessToken()
}
```

If the API returns 401 or 403, refresh sign-in state, check the requested scope, and confirm the user has permission on the target channel/resource.
