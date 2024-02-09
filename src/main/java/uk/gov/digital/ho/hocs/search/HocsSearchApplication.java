package uk.gov.digital.ho.hocs.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
public class HocsSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HocsSearchApplication.class, args);
    }

    @PreDestroy
    public void stop() {
        log.info("Stopping gracefully");
    }

}
