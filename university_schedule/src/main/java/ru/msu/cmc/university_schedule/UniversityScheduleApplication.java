package ru.msu.cmc.university_schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "ru.msu.cmc.university_schedule.DAO")
public class UniversityScheduleApplication {

	public static void main(String[] args) {
		SpringApplication.run(UniversityScheduleApplication.class, args);
	}

}
