package br.com.mkcf.cepapi.service;


import br.com.mkcf.cepapi.client.CorreiosClient;
import br.com.mkcf.cepapi.client.WiremockClient;
import br.com.mkcf.cepapi.exception.CepNotFoundException;
import br.com.mkcf.cepapi.model.dto.CepResponse;
import br.com.mkcf.cepapi.messaging.SqsPublisher;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CepService {

    private Logger logger = LoggerFactory.getLogger(CepService.class);

    private final MeterRegistry meterRegistry;

    private final WiremockClient correiosClient;
    private final CorreiosClient fallbackOperation;
    private final SqsPublisher sqsPublisher;

    public CepService(MeterRegistry meterRegistry, WiremockClient correiosClient, CorreiosClient fallbackOperation, SqsPublisher sqsPublisher) {
        this.meterRegistry = meterRegistry;
        this.correiosClient = correiosClient;
        this.fallbackOperation = fallbackOperation;
        this.sqsPublisher = sqsPublisher;
    }

    @CircuitBreaker(name = "correiosApi", fallbackMethod = "fallbackConsultaCep")
    public CepResponse consultarCep(String cep) {
        try {
            logger.info("Iniciando consulta para o CEP: {}", cep);
            CepResponse cepResponse = correiosClient.consultarCep(cep);
            meterRegistry.counter("cep.consultas.sucesso").increment();
            enviarMensagemParaSQS(cepResponse, cep);
            logger.info("Consulta para o CEP {} concluída com sucesso.", cep);
            return cepResponse;
        } catch (Exception e) {
            meterRegistry.counter("cep.service.falha").increment(); // Incrementa no erro
            logger.error("Erro ao consultar o CEP {}: {}", cep, e.getMessage());
            throw e;
        }
    }

    public CepResponse fallbackConsultaCep(String cep, Throwable throwable) {
        meterRegistry.counter("cep.service.fallback").increment(); // Incrementa no fallback

        logger.warn("CircuitBreaker acionado para o CEP {} devido ao erro: {}", cep, throwable.getMessage());
        CepResponse cepResponse = null;

        cepResponse = fallbackOperation.consultarCep(cep);
        if (cepResponse != null && StringUtils.hasText(cepResponse.getLogradouro())) {
            enviarMensagemParaSQS(cepResponse, cep);
        } else {
            logger.error("Fallback não conseguiu recuperar informações para o CEP {}", cep);
            throw new CepNotFoundException("CEP " + cep + " não encontrado.");
        }

        return cepResponse;
    }

    private void enviarMensagemParaSQS(CepResponse cepResponse, String cep) {
        sqsPublisher.sendMessage(cepResponse, cep);
    }
}