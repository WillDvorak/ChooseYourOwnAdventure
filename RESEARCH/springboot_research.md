# Research Report
## Intro to Spring Boot
### Summary of Work
I researched the Spring official “Getting Started” guide for Spring Boot to understand how Spring Boot accelerates web application development. By following the guide, I walked through a minimal Spring Boot app, configuring controllers and endpoints, running the app and writing tests.
### Motivation
Our project will use Spring Boot as the framework connecting the frontend web and the backend database. I wanted a structured, canonical walkthrough to serve as a teaching reference. Also it will provide a restful API for the frontend. The official guide is concise yet covers many core features, so it is a great resource for studying Spring Boot.
### Time Spent
Reading & annotating the guide: ~ 30 minutes
Setting up my environment: ~ 40 minutes
Trying out the code examples: ~ 60 minutes
Cross-checking with Spring Boot docs: ~ 30 minutes
### Results
I first installed the Sprint Boot Initializer https://start.spring.io/ [^3] and follow the guide to install it with Gradle.

Then I create a simple web application following the code on the guide [^1]
```Java
package com.example.springboot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}
```

For the controller: a `@RestController` with `@GetMapping("/")` is used to define a simple HTTP GET endpoint returning plain text. 
`@RestController` is shorthand for `@Controller + @ResponseBody`, making methods return response bodies directly.

In the next part, I create an application class:
```Java
package com.example.springboot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}

}
```

Several Points to make:
`SpringBootApplication` is a composition of three annotations:
`Configuration`
`EnableAutoConfiguration`
`ComponentScan`

The `main()` method invokes `SpringApplication.run(...)` to launch the app and A `CommandLineRunner` bean is used here to inspect bean definitions at startup.

To run the application with Gradle: 
```bash
./gradlew bootRun
```
After startup, a curl request to http://localhost:8080/ yields the “Greetings from Spring Boot!” response.

Last part is the unit test:
First, add the following dependency to `build.gradle` file:
```Java
testImplementation('org.springframework.boot:spring-boot-starter-test')
```
Then write a test file as following:
```Java
package com.example.springboot;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void getHello() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from Spring Boot!")));
	}
}
```
`MockMvc` comes from Spring Test and lets you, through a set of convenient builder classes, send HTTP requests into the `DispatcherServlet` and make assertions about the result. [^2]

### Sources
- Spring Boot Tutorial[^1]
- Serving-Web-Content[^2]
- Spring Boot Initializer[^3]
[^1]: https://spring.io/guides/gs/spring-boot
[^2]: https://spring.io/guides/gs/serving-web-content
[^3]: https://start.spring.io/
