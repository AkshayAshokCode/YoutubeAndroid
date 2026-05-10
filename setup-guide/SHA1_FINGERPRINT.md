# SHA-1 fingerprint and API key restrictions

Use SHA-1 fingerprints to connect your Android app identity to Google API credentials.

## Debug SHA-1

Run:

```bash
./gradlew signingReport
```

Copy the SHA-1 for the `debug` variant.

## Release SHA-1

For Play Store apps, use the Play App Signing certificate SHA-1 from Play Console:

1. Open Play Console.
2. Select your app.
3. Go to **Setup > App integrity**.
4. Copy the **App signing key certificate** SHA-1.

## Restricting an API key

For API-key-only examples, restrict the key by:

- Android package name.
- SHA-1 certificate fingerprint.
- API restriction: YouTube Data API v3 only.

This reduces accidental abuse if the key is extracted from an APK.
