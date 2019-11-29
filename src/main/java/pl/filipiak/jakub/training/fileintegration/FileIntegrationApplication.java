package pl.filipiak.jakub.training.fileintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import pl.filipiak.jakub.training.fileintegration.utils.FileInfoBinding;

@EnableBinding(FileInfoBinding.class)
@SpringBootApplication
public class FileIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileIntegrationApplication.class, args);
	}

}
