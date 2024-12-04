package br.com.mkcf.cepapi.queue;

import br.com.mkcf.cepapi.model.CepResponse;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SqsClientProduceApi {

	private final String queueHost;
	private final SqsTemplate sqsTemplate;

	public SqsClientProduceApi(@Value("${sqs.queue.name}") String queueHost, SqsTemplate sqsTemplate) {
		this.queueHost = queueHost;
		this.sqsTemplate = sqsTemplate;
	}

	public void sendMessage(CepResponse cepResponse) {
		sqsTemplate.send(queueHost, new MessageCreatedEvent(cepResponse,
				new SimpleDateFormat("yyyy/MM/dd hh:mm:ss yyy").format(new Date())));
	}

}
