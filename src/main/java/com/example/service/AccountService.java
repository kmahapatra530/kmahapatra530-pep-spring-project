package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Optional<Account> findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public Optional<Account> authenticate(String username, String password) {
        return accountRepository.findByUsername(username)
            .filter(acc -> acc.getPassword().equals(password));
    }

    public boolean existsById(Integer accountId) {
        return accountRepository.existsById(accountId);
    }
}
