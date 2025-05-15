package br.com.fiap.Challenge_Quod;

import lombok.Builder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Builder(toBuilder = true)
@SpringBootApplication
public class ChallengeQuodApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeQuodApplication.class, args);
	}

}