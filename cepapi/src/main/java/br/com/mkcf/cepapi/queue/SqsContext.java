package br.com.mkcf.cepapi.queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class SqsContext {

	@Bean
	public SqsClient createSqsClient() {
		return SqsClient.builder().region(Region.SA_EAST_1).credentialsProvider(ProfileCredentialsProvider.create())
				.build();
	}

}
