package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticSearchClientConfiguration {

    @Bean
    public ElasticSearchClient elasticSearchSingularClient(ObjectMapper objectMapper,
                                                           RestHighLevelClient client,
                                                           @Value("${aws.es.index-prefix}") String prefix) {
        return new ElasticSearchSingularClient(objectMapper, client, prefix);
    }

}
