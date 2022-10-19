package uk.gov.digital.ho.hocs.search.application.aws.config.elastic;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;

@Configuration
@Profile("aws")
public class AwsElasticConfiguration {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(@Value("${aws.es.host}") String host,
                                      @Value("${aws.es.serviceName}") String serviceName,
                                      @Value("${aws.region}") String region,
                                      @Value("${aws.es.access-key}") String accessKey,
                                      @Value("${aws.es.secret-key}") String secretKey) {
        Aws4Signer signer = Aws4Signer.create();
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey));

        HttpRequestInterceptor interceptor = new AwsRequestSigningApacheInterceptor(serviceName, signer,
            credentialsProvider, region);
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, -1, "https")).setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder.useSystemProperties().addInterceptorLast(interceptor));

        return new RestHighLevelClient(builder);
    }

}
