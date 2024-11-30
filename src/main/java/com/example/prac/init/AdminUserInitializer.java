package com.example.prac.init;

import com.example.prac.model.authEntity.Role;
import com.example.prac.model.authEntity.User;
import com.example.prac.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdminUser("user1", "password1");
        createAdminUser("username", "password");
    }

    private void createAdminUser(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN) // Предполагается, что роль ADMIN существует в вашем перечислении Role
                    .build();
            userRepository.save(user);
            System.out.println("Admin user '" + username + "' created.");
        } else {
            System.out.println("Admin user '" + username + "' already exists.");
        }
    }
}
