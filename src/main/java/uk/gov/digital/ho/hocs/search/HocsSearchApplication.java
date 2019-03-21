package uk.gov.digital.ho.hocs.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class HocsSearchApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(HocsSearchApplication.class, args);
        } catch (Exception e) {
            log.warn(e.toString());
        }
    }

}
