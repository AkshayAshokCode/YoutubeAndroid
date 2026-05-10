# Subscriptions

OAuth-required feature guide.

## Scope

Subscribe and unsubscribe to channels with explicit user consent.

## Implementation notes

- Requires Google Sign-In and a fresh access token.
- Should use the smallest YouTube OAuth scope needed for the operation.
- Must surface API quota errors, permission errors, and user-revoked-consent errors clearly.
- Keep request/response models and repositories in this folder when implemented.
