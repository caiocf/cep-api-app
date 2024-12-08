package br.com.mkcf.cepapi.controller;

import br.com.mkcf.cepapi.exception.CepNotFoundException;
import br.com.mkcf.cepapi.exception.GlobalExceptionHandler;
import br.com.mkcf.cepapi.model.dto.CepResponse;
import br.com.mkcf.cepapi.service.CepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CepControllerTest {

    @Mock
    private CepService cepService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CepController cepController = new CepController(cepService);
        mockMvc = MockMvcBuilders.standaloneSetup(cepController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Adiciona o GlobalExceptionHandler
                .build();
    }

    @Test
    void consultarCep_DeveRetornarCepResponse_QuandoCepValido() throws Exception {
        // Arrange
        String cep = "12345678";
        CepResponse expectedResponse = new CepResponse();
        expectedResponse.setLogradouro("Rua Exemplo");
        when(cepService.consultarCep(cep)).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/ceps/{cep}", cep))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Rua Exemplo"));

        verify(cepService, times(1)).consultarCep(cep);
    }

    @Test
    void consultarCep_DeveRetornarBadRequest_QuandoCepInvalido() throws Exception {
        // Arrange
        String cep = "1234"; // CEP inválido

        // Act & Assert
        mockMvc.perform(get("/api/v1/ceps/{cep}", cep))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultarCep_DeveRetornarNotFound_QuandoCepNaoEncontrado() throws Exception {
        // Arrange
        String cep = "12345678";
        when(cepService.consultarCep(cep)).thenThrow(new CepNotFoundException("CEP não encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/ceps/{cep}", cep))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Cep não encontrado!"));

        verify(cepService, times(1)).consultarCep(cep);
    }

    @Test
    void consultarCep_DeveRetornarInternalServerError_QuandoErroGenerico() throws Exception {
        // Arrange
        String cep = "12345678";
        when(cepService.consultarCep(cep)).thenThrow(new RuntimeException("Erro interno"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/ceps/{cep}", cep))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro inesperado. Por favor, tente novamente mais tarde."));

        verify(cepService, times(1)).consultarCep(cep);
    }
}