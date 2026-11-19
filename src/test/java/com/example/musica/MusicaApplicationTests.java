package com.example.musica;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:teste")
class MusicaApplicationTests {

	@Test
	void contextLoads() {
	}

}
