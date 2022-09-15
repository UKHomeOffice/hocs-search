package uk.gov.digital.ho.hocs.search.application.aws.config.elastic;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("aws")
public class AwsElasticConfiguration {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(@Value("${aws.es.host}") String host,
                                      @Value("${aws.es.serviceName}") String serviceName,
                                      @Value("${aws.region}") String region,
                                      @Value("${aws.es.access-key}") String accessKey,
                                      @Value("${aws.es.secret-key}") String secretKey) {

        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(accessKey, secretKey));

        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);

        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer,
            credentialsProvider);
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, -1, "https"));
        builder.setHttpClientConfigCallback(
            httpClientBuilder -> httpClientBuilder.useSystemProperties().addInterceptorLast(interceptor));

        return new RestHighLevelClient(builder);
    }

}
