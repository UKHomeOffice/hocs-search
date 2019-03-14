package uk.gov.digital.ho.hocs.search.application;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.geo.CustomGeoModule;

import java.io.IOException;

@Configuration
public class ElasticSearchConfiguration {

  @Bean
  public ElasticsearchTemplate elasticsearchTemplate(Client client) {
    return new ElasticsearchTemplate(client, new CustomEntityMapper());
  }

  public static class CustomEntityMapper implements EntityMapper {

    private final ObjectMapper objectMapper;

    public CustomEntityMapper() {
      objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
      objectMapper.registerModule(new CustomGeoModule());
      objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String mapToString(Object object) throws IOException {
      return objectMapper.writeValueAsString(object);
    }

    @Override
    public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
      return objectMapper.readValue(source, clazz);
    }
  }
}