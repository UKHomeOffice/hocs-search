package uk.gov.digital.ho.hocs.search.application.aws;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"awselastic"})
public class AwsElasticSearchConfiguration {

  @Value("${elasticsearch.host}")
  private String host;

  @Value("${elasticsearch.port}")
  private int port;

  @Value("${elasticsearch.serviceName}")
  private String serviceName;

  @Value("${aws.sqs.region}")
  private String region;

  @Value("${elasticsearch.access.key}")
  private String accessKey;

  @Value("${elasticsearch.secret.key}")
  private String secretKey;

  @Bean(destroyMethod = "close")
  public RestHighLevelClient client() {

    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));

    AWS4Signer signer = new AWS4Signer();
    signer.setServiceName(serviceName);
    signer.setRegionName(region);

    HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);

    return new RestHighLevelClient(RestClient.builder(new HttpHost(host, port)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));

  }

}