# Live chat

OAuth-required feature guide.

## Scope

Read live chat messages, respect polling intervals, post chat messages, and handle disabled/ended chat states.

## Implementation notes

- Requires Google Sign-In and a fresh access token.
- Should use the smallest YouTube OAuth scope needed for the operation.
- Must surface API quota errors, permission errors, and user-revoked-consent errors clearly.
- Keep request/response models and repositories in this folder when implemented.
