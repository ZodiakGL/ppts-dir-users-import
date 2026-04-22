package ru.sfera.users.model;

public enum ActivityStatus {
    A,
    I,
    T;

    public boolean isActive() {
        return this == A;
    }

    public boolean isInactive() {
        return this == I;
    }

    public boolean isTest() {
        return this == T;
    }
}
