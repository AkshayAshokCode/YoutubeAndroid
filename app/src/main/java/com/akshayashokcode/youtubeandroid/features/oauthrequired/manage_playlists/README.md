# Manage playlists

OAuth-required feature guide.

## Scope

Create playlists, add videos, reorder items, and remove videos from playlists.

## Implementation notes

- Requires Google Sign-In and a fresh access token.
- Should use the smallest YouTube OAuth scope needed for the operation.
- Must surface API quota errors, permission errors, and user-revoked-consent errors clearly.
- Keep request/response models and repositories in this folder when implemented.
