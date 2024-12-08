package br.com.mkcf.cepapi.messaging;

import br.com.mkcf.cepapi.model.dto.CepResponse;
import br.com.mkcf.cepapi.model.dto.SqsMessageProcessor;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SqsPublisherTest {

    private static final String QUEUE_NAME = "test-queue";

    private SqsPublisher sqsPublisher;

    @Mock
    private SqsTemplate sqsTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sqsPublisher = new SqsPublisher(QUEUE_NAME, sqsTemplate);
    }

    @Test
    void testSendMessage() {
        // Arrange
        CepResponse cepResponse = new CepResponse();
        String codigoCep = "12345";

        // Act
        sqsPublisher.sendMessage(cepResponse, codigoCep);

        // Assert
        ArgumentCaptor<SqsMessageProcessor> messageCaptor = ArgumentCaptor.forClass(SqsMessageProcessor.class);
        verify(sqsTemplate, times(1)).send(eq(QUEUE_NAME), messageCaptor.capture());

        SqsMessageProcessor capturedMessage = messageCaptor.getValue();
        assertEquals(cepResponse, capturedMessage.cep());
        assertEquals(codigoCep, capturedMessage.codigoCep());
    }

    @Test
    void testSendMessageWithNullCepResponse() {
        // Arrange
        CepResponse cepResponse = null;
        String codigoCep = "12345";

        // Act
        sqsPublisher.sendMessage(cepResponse, codigoCep);

        // Assert
        ArgumentCaptor<SqsMessageProcessor> messageCaptor = ArgumentCaptor.forClass(SqsMessageProcessor.class);
        verify(sqsTemplate, times(1)).send(eq(QUEUE_NAME), messageCaptor.capture());

        SqsMessageProcessor capturedMessage = messageCaptor.getValue();
        assertEquals(cepResponse, capturedMessage.cep());
        assertEquals(codigoCep, capturedMessage.codigoCep());
    }

    @Test
    void testSendMessageWithNullCodigoCep() {
        // Arrange
        CepResponse cepResponse = new CepResponse();
        String codigoCep = null;

        // Act
        sqsPublisher.sendMessage(cepResponse, codigoCep);

        // Assert
        ArgumentCaptor<SqsMessageProcessor> messageCaptor = ArgumentCaptor.forClass(SqsMessageProcessor.class);
        verify(sqsTemplate, times(1)).send(eq(QUEUE_NAME), messageCaptor.capture());

        SqsMessageProcessor capturedMessage = messageCaptor.getValue();
        assertEquals(cepResponse, capturedMessage.cep());
        assertEquals(codigoCep, capturedMessage.codigoCep());
    }

    @Test
    void testSendMessageWithNullParameters() {
        // Arrange
        CepResponse cepResponse = null;
        String codigoCep = null;

        // Act
        sqsPublisher.sendMessage(cepResponse, codigoCep);

        // Assert
        ArgumentCaptor<SqsMessageProcessor> messageCaptor = ArgumentCaptor.forClass(SqsMessageProcessor.class);
        verify(sqsTemplate, times(1)).send(eq(QUEUE_NAME), messageCaptor.capture());

        SqsMessageProcessor capturedMessage = messageCaptor.getValue();
        assertEquals(cepResponse, capturedMessage.cep());
        assertEquals(codigoCep, capturedMessage.codigoCep());;
    }
}