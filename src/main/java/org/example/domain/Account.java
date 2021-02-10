package org.example.domain;

public class Account {
    private DomainAccountKey accountKey;
    private String name;

    public Account(DomainAccountKey accountKey, String name) {
        this.accountKey = accountKey;
        this.name = name;
    }

    public Account() {
    }

    public DomainAccountKey getAccountKey() {
        return accountKey;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountKey=" + accountKey +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (!accountKey.equals(account.accountKey)) return false;
        return name.equals(account.name);
    }

    @Override
    public int hashCode() {
        int result = accountKey.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
