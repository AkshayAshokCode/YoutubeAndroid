# Comments

OAuth-required feature guide.

## Scope

Post, list, moderate, and delete comments/comment threads with account-scoped permissions.

## Implementation notes

- Requires Google Sign-In and a fresh access token.
- Should use the smallest YouTube OAuth scope needed for the operation.
- Must surface API quota errors, permission errors, and user-revoked-consent errors clearly.
- Keep request/response models and repositories in this folder when implemented.
