package uk.gov.digital.ho.hocs.search.application.aws;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AWSRequestSigningApacheInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@Profile({"awselastic"})
public class AwsElasticSearchConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

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

        RestClientBuilder.HttpClientConfigCallback rcb = new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.useSystemProperties().setProxy(new HttpHost("https://hocs-outbound-proxy.cs-dev.svc.cluster.local:31290"));
                return httpClientBuilder;
            }
        };

        RestClientBuilder builder = RestClient.builder( new HttpHost(host,-1, "HTTPS"));
        builder.setHttpClientConfigCallback(rcb);

        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        builder.setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor));

        return new RestHighLevelClient(builder);

    }

}