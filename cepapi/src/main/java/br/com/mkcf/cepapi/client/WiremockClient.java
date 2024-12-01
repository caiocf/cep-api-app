package br.com.mkcf.cepapi.client;

import br.com.mkcf.cepapi.model.CepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class WiremockClient {

    private final RestClient restClient;

    @Value("${api.wiremock.url}")
    private String urlApiWireMock;

    public CepResponse consultarCep(String cep) {
        return restClient.get().uri(urlApiWireMock, cep).retrieve().body(CepResponse.class);
    }
}