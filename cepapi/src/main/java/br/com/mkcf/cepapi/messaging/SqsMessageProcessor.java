package br.com.mkcf.cepapi.messaging;

import br.com.mkcf.cepapi.model.CepResponse;

public record SqsMessageProcessor(CepResponse cep, String codigoCep){

}
