package org.example.domain;

import java.util.Collection;

public interface AccountDao {
    Collection<Account> findAll();
}
