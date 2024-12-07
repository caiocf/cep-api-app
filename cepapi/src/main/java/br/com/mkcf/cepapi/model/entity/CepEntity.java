package br.com.mkcf.cepapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@Builder
public class CepEntity {

    private String cep;
    private String dataCadastrado;
    private Long expirationTimestamp;

    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;

    @DynamoDbPartitionKey
    public String getCep() {
        return cep;
    }

    @DynamoDbSortKey
    public String getDataCadastrado() {
        return dataCadastrado;
    }

    @DynamoDbAttribute("expirationTimestamp")
    public Long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "LocalidadeIndex")
    @DynamoDbAttribute("localidade")
    public String getLocalidade() {
        return localidade;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setDataCadastrado(String dataCadastrado) {
        this.dataCadastrado = dataCadastrado;
    }

    public void setExpirationTimestamp(Long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
