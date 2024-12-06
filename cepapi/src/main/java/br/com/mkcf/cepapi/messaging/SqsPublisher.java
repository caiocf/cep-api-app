package br.com.mkcf.cepapi.messaging;

import br.com.mkcf.cepapi.model.CepResponse;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SqsPublisher {

	private final String queueHost;
	private final SqsTemplate sqsTemplate;

	public SqsPublisher(@Value("${sqs.queue.name}") String queueHost, SqsTemplate sqsTemplate) {
		this.queueHost = queueHost;
		this.sqsTemplate = sqsTemplate;
	}

	public void sendMessage(CepResponse cepResponse, String codigoCep) {
		sqsTemplate.send(queueHost, new SqsMessageProcessor(cepResponse,codigoCep));
	}

}
