package br.com.mkcf.cepapi.messaging;

import br.com.mkcf.cepapi.model.dto.CepResponse;
import br.com.mkcf.cepapi.model.dto.SqsMessageProcessor;
import br.com.mkcf.cepapi.model.entity.CepEntity;
import br.com.mkcf.cepapi.repository.CepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SqsListenerTest {

    private CepRepository cepRepository;
    private SqsListener sqsListener;

    @BeforeEach
    void setUp() {
        cepRepository = mock(CepRepository.class);
        sqsListener = new SqsListener(cepRepository);
    }

    @Test
    void testOnMessage() {
        // Arrange
        CepResponse cepDto = new CepResponse("Rua Teste","","Bairro Teste","Uberlandia","MG");
        SqsMessageProcessor message = new SqsMessageProcessor(cepDto,"12345678");

        ArgumentCaptor<CepEntity> captor = ArgumentCaptor.forClass(CepEntity.class);

        // Act
        sqsListener.onMessage(message);

        // Assert
        verify(cepRepository, times(1)).save(captor.capture());
        doNothing().when(cepRepository).save(any(CepEntity.class));

        CepEntity savedEntity = captor.getValue();

        assertEquals("12345678", savedEntity.getCep());
        assertEquals("Rua Teste", savedEntity.getLogradouro());
        assertEquals("", savedEntity.getComplemento());
        assertEquals("Bairro Teste", savedEntity.getBairro());
        assertEquals("Uberlandia", savedEntity.getLocalidade());
        assertEquals("MG", savedEntity.getUf());
        assertEquals(Instant.now().plus(30, ChronoUnit.DAYS).getEpochSecond(), savedEntity.getExpirationTimestamp(), 5); // Allowing a small delta for time differences
    }

    @Test
    void testOnMessageLogsMessage() {
        // Arrange
        SqsListener mockListener = mock(SqsListener.class);

        CepResponse cepDto = new CepResponse("Rua Teste","","Bairro Teste","Uberlandia","MG");
        SqsMessageProcessor message = new SqsMessageProcessor(cepDto,"12345678");


        // Act
        mockListener.onMessage(message);

        verify(mockListener, times(1)).onMessage(message);
    }
}