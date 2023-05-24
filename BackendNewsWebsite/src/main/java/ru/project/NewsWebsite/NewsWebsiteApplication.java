package ru.project.NewsWebsite;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.services.PostService;
import ru.project.NewsWebsite.services.RegistrationService;
import ru.project.NewsWebsite.services.TagService;

import java.time.LocalDateTime;
import java.time.Month;

@SpringBootApplication
public class NewsWebsiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsWebsiteApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RegistrationService registrationService, PostService postService, TagService tagService){

		return args -> {

			Post news1 = new Post("Первая новость", "Все любят животных", "https://drive.google.com/file/d/1xI-xacC5NcM_5bTre2C3NwRSFiheFp7L/view?usp=sharing", LocalDateTime.now());
			Post news2 = new Post("Ещё одна новость.", "Коты и кошки очень милые.", "https://drive.google.com/file/d/12F9Kz7CPqZ9MMa9rykDi4HsjCLfCcNfI/view", LocalDateTime.now());
			Post news3 = new Post("Хорошая новость.", "Собачки очень милые тоже.", "https://drive.google.com/file/d/1Q5pX2OHbzIY07JptST0TxGfX3Hh7fn2m/view?usp=sharing", LocalDateTime.now());
			Post news4 = new Post("Про ёжиков.", "Ёжики почти как кошки. Только ёжики.", "https://drive.google.com/file/d/1K4LVGGX_v_m3G0fTKBMJvcyqz-koaaaq/view?usp=sharing", LocalDateTime.now());
			Post news5 = new Post("Hot news!", "Природу нужно беречь.", "https://drive.google.com/file/d/1xI-xacC5NcM_5bTre2C3NwRSFiheFp7L/view?usp=sharing", LocalDateTime.now());
			Post news6 = new Post("Устаревшая новость.", "Здесь нет ничего важного.",  "https://drive.google.com/file/d/1K4LVGGX_v_m3G0fTKBMJvcyqz-koaaaq/view?usp=sharing", LocalDateTime.of(2023, Month.MARCH, 3, 12, 30));

			postService.save(news1);
			postService.save(news1);
			postService.save(news2);
			postService.save(news3);
			postService.save(news4);
			postService.save(news5);
			postService.save(news6);

			Person Nataly = new Person("Наталья", "Евгенина", "nataly@gmail.com", "1234");
			Person Xenia = new Person("Ксения", "Львова", "xeniaL@gmail.com", "0456");
			Person Katy = new Person("Екатерина", "Смирнова", "katy@gmail.com", "77711");
			Person Test = new Person("Тестовый", "Пользователь", "test@gmail.com", "Test");
			Person Ann = new Person("Анна", "Ахматова", "anna@gmail.com", "0192");
			Person Daria = new Person("Дарья", "Потомова", "daria@gmail.com", "82846");
			Person Danil = new Person("Данил", "Комиссаров", "dan@gmail.com", "qwerty");
			Katy.setRole("ROLE_ADMIN");

			registrationService.register(Nataly);
			registrationService.register(Xenia);
			registrationService.register(Katy);
			registrationService.register(Test);
			registrationService.register(Ann);
			registrationService.register(Danil);
			registrationService.register(Daria);

		};
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

}
