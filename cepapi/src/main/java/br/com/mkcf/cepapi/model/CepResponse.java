package br.com.mkcf.cepapi.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CepResponse {
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
}