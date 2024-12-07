package br.com.mkcf.cepapi.repository;

import br.com.mkcf.cepapi.model.entity.CepEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Repository
public class CepRepository {

    private final DynamoDbTable<CepEntity> table;

    public CepRepository(DynamoDbEnhancedClient enhancedClient) {
        this.table = enhancedClient.table("ConsultaCEP", TableSchema.fromBean(CepEntity.class));
    }

    public void save(CepEntity cepEntity) {
        table.putItem(cepEntity);
    }

    public CepEntity findById(String cep, String dataCadastrado) {
        return table.getItem(r -> r.key(Key.builder()
                .partitionValue(cep)
                .sortValue(dataCadastrado)
                .build()));
    }

    public List<CepEntity> findById(String cep) {

        return table.query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(cep)
                        .build())))
                .items()
                .stream()
                .toList();
    }

    public List<Page<CepEntity>> findByLocalidade(String localidade) {

        var index = table.index("LocalidadeIndex");
        return index.query(r -> r.queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(localidade)
                        .build()))).stream().toList();
    }


}
