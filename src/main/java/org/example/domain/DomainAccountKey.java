package org.example.domain;

public class DomainAccountKey {
    private long accountId;

    public DomainAccountKey(long accountId) {
        this.accountId = accountId;
    }

    public DomainAccountKey() {
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "DomainAccountKey{" +
                "accountId=" + accountId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainAccountKey that = (DomainAccountKey) o;

        return accountId == that.accountId;
    }

    @Override
    public int hashCode() {
        return (int) (accountId ^ (accountId >>> 32));
    }
}
