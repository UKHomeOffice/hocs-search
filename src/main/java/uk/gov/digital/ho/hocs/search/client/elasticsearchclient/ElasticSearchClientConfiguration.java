package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ElasticSearchClientConfiguration {

    @Bean(value = "elasticSearchClient")
    @ConditionalOnProperty(prefix = "aws.es", name = "version", havingValue = "6")
    @Deprecated(forRemoval = true)
    public ElasticSearchClient elasticSearch6Client(ObjectMapper objectMapper,
                                                    RestHighLevelClient client,
                                                    @Value("${aws.es.index-prefix}") String prefix) {
        return new ElasticSearchClient(objectMapper, client, prefix, "caseData");
    }

    @Bean(value = "elasticSearchClient")
    @ConditionalOnProperty(prefix = "aws.es", name = "version", havingValue = "7")
    public ElasticSearchClient elasticSearch7Client(ObjectMapper objectMapper,
                                                    RestHighLevelClient client,
                                                    @Value("${aws.es.index-prefix}") String prefix) {
        return new ElasticSearchClient(objectMapper, client, prefix, "_doc");
    }

}
