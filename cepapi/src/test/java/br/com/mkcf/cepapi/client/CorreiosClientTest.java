package br.com.mkcf.cepapi.client;

import br.com.mkcf.cepapi.model.dto.CepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorreiosClientTest {

    @Mock
    private RestClient restClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private RestClient.ResponseSpec responseSpecMock;

    private CorreiosClient correiosClient;

    @BeforeEach
    void setUp() {
        correiosClient = new CorreiosClient(restClient, "http://api.correios.com/cep/{cep}");
        when(restClient.get()).thenReturn(requestHeadersUriSpecMock);
    }

    @Test
    void consultarCep_DeveRetornarCepResponse_QuandoApiRetornaDados() {
        // Arrange
        String cep = "12345678";
        CepResponse expectedResponse = new CepResponse();
        expectedResponse.setLogradouro("Rua Exemplo");

        when(requestHeadersUriSpecMock.uri("http://api.correios.com/cep/{cep}", cep)).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.body(CepResponse.class)).thenReturn(expectedResponse);

        // Act
        CepResponse actualResponse = correiosClient.consultarCep(cep);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(restClient, times(1)).get();
    }

    @Test
    void consultarCep_DeveLancarExcecao_QuandoApiFalha() {
        // Arrange
        String cep = "12345678";
        when(requestHeadersUriSpecMock.uri("http://api.correios.com/cep/{cep}", cep)).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.body(CepResponse.class)).thenThrow(new RuntimeException("Erro na API"));;

        // Act & Assert
        try {
            correiosClient.consultarCep(cep);
        } catch (RuntimeException e) {
            assertEquals("Erro na API", e.getMessage());
        }

        verify(restClient, times(1)).get();
    }
}