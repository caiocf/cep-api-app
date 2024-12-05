package br.com.mkcf.cepapi.repository;

import br.com.mkcf.cepapi.model.ConsultaCEP;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class ConsultaCEPRepository {

    private final DynamoDbTable<ConsultaCEP> table;

    public ConsultaCEPRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ConsultaCEP", TableSchema.fromBean(ConsultaCEP.class));
    }

    public void save(ConsultaCEP consultaCEP) {
        table.putItem(consultaCEP);
    }

    public ConsultaCEP findById(String cep, Long timestamp) {
        return table.getItem(r -> r.key(Key.builder()
                .partitionValue(cep)
                .sortValue(timestamp)
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


}
