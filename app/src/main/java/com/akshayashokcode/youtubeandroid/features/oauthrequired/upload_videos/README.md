# Upload videos

OAuth-required feature guide.

## Scope

Resumable upload flow, metadata, privacy status, tags, thumbnails, and post-upload verification.

## Implementation notes

- Requires Google Sign-In and a fresh access token.
- Should use the smallest YouTube OAuth scope needed for the operation.
- Must surface API quota errors, permission errors, and user-revoked-consent errors clearly.
- Keep request/response models and repositories in this folder when implemented.
