package uk.gov.digital.ho.hocs.search.application.aws.config.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@EnableSqs
@Configuration
@Profile("localstack & consumer")
public class LocalStackConfiguration {

    @Primary
    @Bean
    public AmazonSQSAsync awsSqsClient(@Value("${aws.sqs.search.url}") String awsBaseUrl,
                                       @Value("${aws.sqs.search.access-key}") String accessKey,
                                       @Value("${aws.sqs.search.secret-key}") String secretKey,
                                       @Value("${aws.region}") String region) {
        return AmazonSQSAsyncClientBuilder.standard().withCredentials(
            new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey))).withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(awsBaseUrl, region)).build();
    }

    @Primary
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs) {
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();

        factory.setAmazonSqs(amazonSqs);
        factory.setMaxNumberOfMessages(1);
        factory.setWaitTimeOut(5);

        return factory;
    }

}
