package net.simnacher.hometodo.config;

import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("Florian"));
                userRepository.save(new User("Christiane"));
                userRepository.save(new User("Elisabeth"));
                userRepository.save(new User("Johanna"));
                System.out.println("Default users created");
            }
        };
    }
}
