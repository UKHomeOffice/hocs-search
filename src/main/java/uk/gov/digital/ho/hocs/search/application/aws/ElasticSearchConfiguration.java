package uk.gov.digital.ho.hocs.search.application.aws;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local"})
public class ElasticSearchConfiguration {

  @Value("${elasticsearch.host}")
  private String host;

  @Value("${elasticsearch.port}")
  private int port;

  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {

    return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)));
  }

}