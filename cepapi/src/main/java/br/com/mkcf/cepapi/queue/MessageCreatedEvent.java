package br.com.mkcf.cepapi.queue;

import br.com.mkcf.cepapi.model.CepResponse;

public record MessageCreatedEvent (CepResponse cep, String date){

}
