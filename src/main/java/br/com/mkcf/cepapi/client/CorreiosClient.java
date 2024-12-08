package br.com.mkcf.cepapi.client;

import br.com.mkcf.cepapi.model.dto.CepResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CorreiosClient {

    private final RestClient restClient;
    private String urlApiCorreios;

    public CorreiosClient(RestClient restClient,  @Value("${api.correios.url}") String urlApiCorreios) {
        this.restClient = restClient;
        this.urlApiCorreios = urlApiCorreios;
    }

    public CepResponse consultarCep(String cep) {
        return restClient.get().uri(urlApiCorreios, cep).retrieve().body(CepResponse.class);
    }
}