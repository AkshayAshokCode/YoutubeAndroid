# youtube-api-examples

A grab-and-go Android Kotlin reference repo for the YouTube Data API v3.

The goal is simple: Android developers should be able to copy one feature folder, add their API key or OAuth token provider, and understand the request, response, quota cost, and edge cases without digging through Google docs first.

## Quick start

### API-key-only features in 30 seconds

1. Create a YouTube Data API v3 key in Google Cloud Console.
2. Add it to `~/.gradle/gradle.properties` or this project `gradle.properties`:

   ```properties
   YT_API_KEY=your_api_key_here
   ```

3. Open one folder in [`API_KEY_ONLY`](API_KEY_ONLY), copy the implementation and ViewModel into your app, and wire the Retrofit interface to your Retrofit instance.
4. Use the folder's `example-response.json` to build UI before hitting the real API.

See [`setup-guide/API_KEY_SETUP.md`](setup-guide/API_KEY_SETUP.md) for the short setup and [`setup-guide/SHA1_FINGERPRINT.md`](setup-guide/SHA1_FINGERPRINT.md) for key restriction guidance.

### OAuth-required features

1. Configure Google Sign-In and an OAuth client.
2. Request the smallest YouTube scope needed by the feature.
3. Provide an access-token function to the feature implementation.
4. Copy the feature folder from [`OAUTH_REQUIRED`](OAUTH_REQUIRED).

See [`setup-guide/OAUTH_SETUP.md`](setup-guide/OAUTH_SETUP.md) for the Android OAuth checklist.

### UI flow plan

The runnable sample app starts with a root selection screen where developers choose **API Key APIs** or **OAuth APIs (Advanced)**. This core product structure and the detailed navigation/screen plan are documented in [`docs/UI_FLOW.md`](docs/UI_FLOW.md).

## Repository structure

```text
youtube-api-examples/
├── README.md
├── API_KEY_ONLY/
│   ├── Search Videos/
│   ├── Get Video Details/
│   ├── Get Channel Info/
│   ├── Browse Playlists/
│   └── Get Categories and Regions/
├── OAUTH_REQUIRED/
│   ├── Upload Video/
│   ├── Live Streaming/
│   ├── Live Chat/
│   ├── Post Comments/
│   ├── Manage Playlists/
│   ├── Subscribe to Channels/
│   └── Manage Captions/
├── setup-guide/
│   ├── API_KEY_SETUP.md
│   ├── OAUTH_SETUP.md
│   └── SHA1_FINGERPRINT.md
└── app/
    └── Android project shell with shared dependencies and existing sample code
```

## Feature folder contract

Every feature folder follows the same shape:

```text
Feature Name/
├── FeatureName.kt              # actual API implementation + request/response models
├── FeatureNameViewModel.kt     # UI logic separated from API logic
├── README.md                   # what it does, quota cost, edge cases
└── example-response.json       # sample API response shape
```

This structure keeps auth boundaries obvious:

- Use `API_KEY_ONLY` when the endpoint reads public data and does not need a user account.
- Use `OAUTH_REQUIRED` when the endpoint reads or mutates the signed-in user's YouTube account.

## Current API-key-only examples

| Folder | Endpoint(s) | Auth |
|---|---|---|
| [`Search Videos`](API_KEY_ONLY/Search%20Videos) | `search.list` | API key |
| [`Get Video Details`](API_KEY_ONLY/Get%20Video%20Details) | `videos.list` | API key |
| [`Get Channel Info`](API_KEY_ONLY/Get%20Channel%20Info) | `channels.list` | API key |
| [`Browse Playlists`](API_KEY_ONLY/Browse%20Playlists) | `playlists.list`, `playlistItems.list` | API key |
| [`Get Categories and Regions`](API_KEY_ONLY/Get%20Categories%20and%20Regions) | `videoCategories.list`, `i18nRegions.list` | API key |

## Current OAuth-required examples

| Folder | Endpoint(s) | Auth |
|---|---|---|
| [`Upload Video`](OAUTH_REQUIRED/Upload%20Video) | `videos.insert` | OAuth |
| [`Live Streaming`](OAUTH_REQUIRED/Live%20Streaming) | `liveBroadcasts.*`, `liveStreams.*` | OAuth |
| [`Live Chat`](OAUTH_REQUIRED/Live%20Chat) | `liveChatMessages.list`, `liveChatMessages.insert` | OAuth |
| [`Post Comments`](OAUTH_REQUIRED/Post%20Comments) | `commentThreads.insert`, `comments.delete` | OAuth |
| [`Manage Playlists`](OAUTH_REQUIRED/Manage%20Playlists) | `playlists.insert`, `playlistItems.*` | OAuth |
| [`Subscribe to Channels`](OAUTH_REQUIRED/Subscribe%20to%20Channels) | `subscriptions.insert`, `subscriptions.delete` | OAuth |
| [`Manage Captions`](OAUTH_REQUIRED/Manage%20Captions) | `captions.*` | OAuth |

## Android app module

The `app/` module remains available as a runnable Android project shell with shared Gradle dependencies, Hilt setup, Retrofit/Moshi/OkHttp dependencies, and existing core examples. The top-level folders are intentionally optimized for copy-paste learning and direct integration into other Android apps.
