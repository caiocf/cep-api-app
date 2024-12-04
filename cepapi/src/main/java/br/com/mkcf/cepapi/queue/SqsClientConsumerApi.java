package br.com.mkcf.cepapi.queue;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SqsClientConsumerApi {

	private Logger logger = LoggerFactory.getLogger(SqsClientConsumerApi.class);

	@SqsListener(queueNames  = "${sqs.queue.name}")
	public void onMessage(Message<?> message) {
	//public void onMessage(Message<MessageCreatedEvent > message) {
		logger.info("Mensagem recebida fila SQS: {}", message.getPayload());
	}

}
