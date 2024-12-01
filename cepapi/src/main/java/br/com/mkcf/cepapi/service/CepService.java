package br.com.mkcf.cepapi.service;


import org.springframework.stereotype.Service;

import br.com.mkcf.cepapi.client.CorreiosClient;
import br.com.mkcf.cepapi.client.WiremockClient;
import br.com.mkcf.cepapi.model.CepResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CepService {
    private final CorreiosClient correiosClient;
    private final WiremockClient fallbackOperation;

    public CepService(CorreiosClient correiosClient,WiremockClient fallbackOperation) {
        this.correiosClient = correiosClient;
		this.fallbackOperation = fallbackOperation;
    }

    @CircuitBreaker(name = "correiosApi", fallbackMethod = "fallbackConsultaCep")
    public CepResponse consultarCep(String cep) {
        return correiosClient.consultarCep(cep);
    }

    public CepResponse fallbackConsultaCep(String cep, Throwable throwable) {
        return fallbackOperation.consultarCep(cep);
    }
}