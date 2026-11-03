package co.edu.uniquindio.bibliometria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Arranque de Spring Boot; dispara la carga del corpus al iniciar.
 */
@SpringBootApplication
public class BibliometriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliometriaApplication.class, args);
    }
}
