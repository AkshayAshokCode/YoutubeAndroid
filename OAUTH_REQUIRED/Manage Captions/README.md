# Manage Captions

## What it does

Lists and manages caption tracks for videos owned by the authenticated user.

## YouTube endpoint

`captions.list + captions.insert/update/delete`

## Auth type

OAuth 2.0 required. API keys are not enough.

## Quota cost

50+ units depending on operation

## Files in this folder

- `ManageCaptions.kt` — actual API implementation and request/response models.
- `ManageCaptionsViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- Requires a signed-in Google account with the correct YouTube scope.
- Handle 401/403 by re-authenticating or requesting the missing scope.
- Account-owned resources can be private, deleted, or restricted by channel permissions.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
