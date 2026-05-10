# Get Categories and Regions

## What it does

Loads assignable video categories and YouTube-supported regions for localized browsing or upload forms.

## YouTube endpoint

`videoCategories.list + i18nRegions.list`

## Auth type

API key only. No Google Sign-In required.

## Quota cost

1 unit per request

## Files in this folder

- `GetCategoriesAndRegions.kt` — actual API implementation and request/response models.
- `GetCategoriesAndRegionsViewModel.kt` — UI state and ViewModel logic separated from API calls.
- `example-response.json` — realistic response shape for quick UI modeling.

## Edge cases

- Categories are region-specific.
- Not every category is assignable.
- Region names can be localized with hl.

## Integration steps

1. Copy this folder into your app module.
2. Wire the `Api` interface into your Retrofit instance.
3. Provide either an API key or OAuth access-token provider.
4. Use the ViewModel state to render loading, content, and error states.
