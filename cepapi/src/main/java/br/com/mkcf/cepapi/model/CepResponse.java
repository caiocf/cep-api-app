package br.com.mkcf.cepapi.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CepResponse {


    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
}