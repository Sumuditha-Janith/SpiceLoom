package lk.ijse.gdse71.spiceloom.config;

import lk.ijse.gdse71.spiceloom.entity.User;
import lk.ijse.gdse71.spiceloom.enums.Role;
import lk.ijse.gdse71.spiceloom.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("Admin")) {
            User admin = new User();
            admin.setUsername("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setAddress("515, Des Moines, Iowa");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}