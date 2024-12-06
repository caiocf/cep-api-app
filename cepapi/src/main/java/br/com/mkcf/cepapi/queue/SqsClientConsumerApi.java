package br.com.mkcf.cepapi.queue;

import br.com.mkcf.cepapi.model.ConsultaCEP;
import br.com.mkcf.cepapi.repository.CEPRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SqsClientConsumerApi {

	private Logger logger = LoggerFactory.getLogger(SqsClientConsumerApi.class);

	private final CEPRepository cepRepository;

	public SqsClientConsumerApi(CEPRepository cepRepository) {
		this.cepRepository = cepRepository;
	}

	@SqsListener(queueNames  = "${sqs.queue.name}")
	public void onMessage(MessageCreatedEvent message) {
		logger.info("Mensagem recebida fila SQS: {}", message);

		ConsultaCEP cep = ConsultaCEP.builder()
				.cep(message.codigoCep())
				.dataCadastrado(DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
				.expirationTimestamp(Instant.now().plus(30, ChronoUnit.DAYS).getEpochSecond())
				.logradouro(message.cep().getLogradouro())
				.complemento(message.cep().getComplemento())
				.bairro(message.cep().getBairro())
				.localidade(message.cep().getLocalidade())
				.uf(message.cep().getUf()).build();

		cepRepository.save(cep);
		logger.info("Cep : {} cadastradoa com sucesso {} ", cep.getCep(),cep.getDataCadastrado() );
		//List<ConsultaCEP> items = cepRepository.findById("38408-072");
		//List<ConsultaCEP> items = cepRepository.findByLocalidade("Uberlândia");
	}

}
