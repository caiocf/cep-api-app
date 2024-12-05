package br.com.mkcf.cepapi.service;


import br.com.mkcf.cepapi.client.CorreiosClient;
import br.com.mkcf.cepapi.client.WiremockClient;
import br.com.mkcf.cepapi.model.CepResponse;
import br.com.mkcf.cepapi.model.ConsultaCEP;
import br.com.mkcf.cepapi.queue.SqsClientProduceApi;
import br.com.mkcf.cepapi.repository.ConsultaCEPRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CepService {

    private Logger logger = LoggerFactory.getLogger(CepService.class);

    private final WiremockClient correiosClient;
    private final CorreiosClient fallbackOperation;
    private final SqsClientProduceApi sqsClientProduceApi;
    private final ConsultaCEPRepository consultaCEPRepository;

    public CepService(WiremockClient correiosClient, CorreiosClient fallbackOperation, SqsClientProduceApi sqsClientProduceApi, ConsultaCEPRepository consultaCEPRepository) {
        this.correiosClient = correiosClient;
        this.fallbackOperation = fallbackOperation;
        this.sqsClientProduceApi = sqsClientProduceApi;
        this.consultaCEPRepository = consultaCEPRepository;
    }

    @CircuitBreaker(name = "correiosApi", fallbackMethod = "fallbackConsultaCep")
    public CepResponse consultarCep(String cep) {

        List<ConsultaCEP> items = consultaCEPRepository.findById("38408-072");
        CepResponse cepResponse = correiosClient.consultarCep(cep);
        sqsClientProduceApi.sendMessage(cepResponse);
        return cepResponse;
    }

    public CepResponse fallbackConsultaCep(String cep, Throwable throwable) {
        logger.warn("Acionado CircuitBreaker");
        CepResponse cepResponse = fallbackOperation.consultarCep(cep);
        sqsClientProduceApi.sendMessage(cepResponse);
        return cepResponse;
    }
}