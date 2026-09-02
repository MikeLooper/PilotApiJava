package com.pilotapi.security;

public enum AuthOutcome {
    AUTHORIZED,
    UNAUTHENTICATED,
    UNKNOWN_USER,
    INSUFFICIENT_ROLE
}
