package org.example.config;

import org.example.model.Branch;
import org.example.repository.BranchRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedBranches(BranchRepository branchRepository) {
        return args -> {
            if (branchRepository.count() == 0) {
                branchRepository.save(new Branch("Downtown", "100 Main St", LocalTime.of(9, 0), LocalTime.of(17, 0), 30));
                branchRepository.save(new Branch("Westside", "200 Oak Ave", LocalTime.of(10, 0), LocalTime.of(18, 0), 30));
                branchRepository.save(new Branch("Airport", "1 Terminal Blvd", LocalTime.of(8, 0), LocalTime.of(16, 0), 20));
            }
        };
    }
}

