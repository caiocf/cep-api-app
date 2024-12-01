package br.com.mkcf.cepapi.service;


import br.com.mkcf.cepapi.client.CorreiosClient;
import br.com.mkcf.cepapi.model.CepResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class CepService {
    private final CorreiosClient correiosClient;

    public CepService(CorreiosClient correiosClient) {
        this.correiosClient = correiosClient;
    }

    @CircuitBreaker(name = "correiosApi", fallbackMethod = "fallbackConsultaCep")
    public CepResponse consultarCep(String cep) {
        return correiosClient.consultarCep(cep);
    }

    public CepResponse fallbackConsultaCep(String cep, Throwable throwable) {
        return new CepResponse("CEP não encontrado", "Fallback ativo","","","");
    }
}