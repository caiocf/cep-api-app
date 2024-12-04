package br.com.mkcf.cepapi.client;

import br.com.mkcf.cepapi.model.CepResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WiremockClient {

    private final RestClient restClient;
    private String urlApiWireMock;

    public WiremockClient(RestClient restClient, @Value("${api.wiremock.url}") String urlApiWireMock) {
        this.restClient = restClient;
        this.urlApiWireMock = urlApiWireMock;
    }

    public CepResponse consultarCep(String cep) {
        return restClient.get().uri(urlApiWireMock, cep).retrieve().body(CepResponse.class);
    }
}