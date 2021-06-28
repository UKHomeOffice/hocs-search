package uk.gov.digital.ho.hocs.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
@EnableRetry
public class HocsSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HocsSearchApplication.class, args);
    }

    @PreDestroy
    public void stop() {
        log.info("hocs-search stopping gracefully");
    }

}
