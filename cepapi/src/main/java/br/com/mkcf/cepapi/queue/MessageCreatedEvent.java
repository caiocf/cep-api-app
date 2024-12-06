package br.com.mkcf.cepapi.queue;

import br.com.mkcf.cepapi.model.CepResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MessageCreatedEvent (CepResponse cep,String codigoCep){

}
