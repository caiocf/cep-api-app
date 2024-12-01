package br.com.mkcf.cepapi.queue;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.mkcf.cepapi.model.CepResponse;
//import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SqsClientApi {

	@Value("${sqs.queue.host}")
	private String queueHost;

	//private final SqsTemplate sqsTemplate;

	public void sendMessage(CepResponse cepResponse) {

	//	sqsTemplate.send(queueHost, new MessageCreatedEvent(cepResponse,
		//		new SimpleDateFormat("yyyy/MM/dd hh:mm:ss yyy").format(new Date())));
	}

}
