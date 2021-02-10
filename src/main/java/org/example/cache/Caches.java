package org.example.cache;

public enum Caches {
    ACCOUNT("Account")
    ;

    private final String name;

    Caches(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
