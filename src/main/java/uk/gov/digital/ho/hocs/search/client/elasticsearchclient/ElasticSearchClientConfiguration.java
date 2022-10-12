package uk.gov.digital.ho.hocs.search.client.elasticsearchclient;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchClientConfiguration {

    @Bean
    public ElasticSearchClient elasticSearchSingularClient(RestHighLevelClient client,
                                                           @Value("${aws.es.mode}") ElasticSearchMode mode,
                                                           @Value("${aws.es.index-prefix}") String prefix,
                                                           @Value("${aws.es.results-limit}") int resultsLimit) {
        if (mode.equals(ElasticSearchMode.MULTIPLE)) {
            return new ElasticSearchMultipleClient(client, prefix, resultsLimit);
        }
        return new ElasticSearchSingularClient(client, prefix, resultsLimit);
    }

}
