package me.ramos.securitystudy.service.impl;

import lombok.RequiredArgsConstructor;
import me.ramos.securitystudy.domain.Account;
import me.ramos.securitystudy.repository.UserRepository;
import me.ramos.securitystudy.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
