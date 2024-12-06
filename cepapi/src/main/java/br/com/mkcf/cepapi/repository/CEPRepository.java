package br.com.mkcf.cepapi.repository;

import br.com.mkcf.cepapi.model.ConsultaCEP;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class CEPRepository {

    private final DynamoDbTable<ConsultaCEP> table;

    public CEPRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ConsultaCEP", TableSchema.fromBean(ConsultaCEP.class));
    }

    public void save(ConsultaCEP consultaCEP) {
        table.putItem(consultaCEP);
    }

    public ConsultaCEP findById(String cep, String dataCadastrado) {
        return table.getItem(r -> r.key(Key.builder()
                .partitionValue(cep)
                .sortValue(dataCadastrado)
                .build()));
    }

    public List<ConsultaCEP> findById(String cep) {

        return table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(cep)
                        .build())))
                .items()
                .stream()
                .toList();
    }

    public List<Page<ConsultaCEP>> findByLocalidade(String localidade) {

        var index = table.index("LocalidadeIndex");
        return index.query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(localidade)
                        .build()))).stream().toList();
    }


}
