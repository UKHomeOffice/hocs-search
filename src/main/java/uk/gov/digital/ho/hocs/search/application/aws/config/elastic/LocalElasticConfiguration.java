package uk.gov.digital.ho.hocs.search.application.aws.config.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("localstack")
public class LocalElasticConfiguration {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(
            @Value("${aws.es.host}") String host,
            @Value("${aws.es.port}") int port) {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)));
    }

}
