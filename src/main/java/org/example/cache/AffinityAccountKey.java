package org.example.cache;

public class AffinityAccountKey {
    private long accountId;

    public AffinityAccountKey(long accountId) {
        this.accountId = accountId;
    }

    public AffinityAccountKey() {
    }

    public long getAccountId() {
        return accountId;
    }

    @Override
    public String toString() {
        return "AffinityAccountKey{" +
                "accountId=" + accountId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AffinityAccountKey that = (AffinityAccountKey) o;

        return accountId == that.accountId;
    }

    @Override
    public int hashCode() {
        return (int) (accountId ^ (accountId >>> 32));
    }
}
