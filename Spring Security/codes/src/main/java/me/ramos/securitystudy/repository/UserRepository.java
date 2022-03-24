package me.ramos.securitystudy.repository;

import me.ramos.securitystudy.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
}
