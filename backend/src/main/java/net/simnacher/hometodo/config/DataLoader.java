package net.simnacher.hometodo.config;

import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(new User("Florian"));
                userRepository.save(new User("Christiane"));
                userRepository.save(new User("Elisabeth"));
                userRepository.save(new User("Johanna"));
                logger.info("Default users created");
            }
        };
    }
}
