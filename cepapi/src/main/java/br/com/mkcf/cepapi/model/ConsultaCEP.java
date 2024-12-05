package br.com.mkcf.cepapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class ConsultaCEP {


    private String cep;
    private String dataCadastrado;

    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private String estado;
    private String regiao;
    private String ibge;
    private String ddd;
    private String siafi;
    private String complemento;
    private Long expirationTimestamp;

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

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setDataCadastrado(String dataCadastrado) {
        this.dataCadastrado = dataCadastrado;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getIbge() {
        return ibge;
    }

    public void setIbge(String ibge) {
        this.ibge = ibge;
    }

    public String getDdd() {
        return ddd;
    }

    public void setDdd(String ddd) {
        this.ddd = ddd;
    }

    public String getSiafi() {
        return siafi;
    }

    public void setSiafi(String siafi) {
        this.siafi = siafi;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setExpirationTimestamp(Long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }
}
