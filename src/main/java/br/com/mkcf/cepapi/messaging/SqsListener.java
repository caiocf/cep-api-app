package br.com.mkcf.cepapi.messaging;

import br.com.mkcf.cepapi.model.dto.SqsMessageProcessor;
import br.com.mkcf.cepapi.model.entity.CepEntity;
import br.com.mkcf.cepapi.repository.CepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class SqsListener {

	private Logger logger = LoggerFactory.getLogger(SqsListener.class);

	private final CepRepository cepRepository;

	public SqsListener(CepRepository cepRepository) {
		this.cepRepository = cepRepository;
	}

	@io.awspring.cloud.sqs.annotation.SqsListener(queueNames  = "${sqs.queue.name}")
	public void onMessage(SqsMessageProcessor message) {
		logger.info("Mensagem recebida fila SQS: {}", message);

		CepEntity cep = CepEntity.builder()
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
		//List<CepEntity> items = cepRepository.findById("38408072");
		//List<CepEntity> items = cepRepository.findByLocalidade("Uberlândia");
	}

}
