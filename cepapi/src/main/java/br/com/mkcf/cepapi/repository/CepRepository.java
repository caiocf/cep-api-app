package br.com.mkcf.cepapi.repository;

import br.com.mkcf.cepapi.model.entity.CepEntity;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

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

        GetItemEnhancedRequest getItemEnhancedRequest = GetItemEnhancedRequest
                .builder()
                .key(Key.builder().partitionValue(cep).sortValue(dataCadastrado).build())
                .build();

        return table.getItem(getItemEnhancedRequest);
    }

    public List<CepEntity> findById(String cep) {

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(cep)
                .build());
        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder().queryConditional(queryConditional).build();

        return table.query(queryEnhancedRequest)
                .items()
                .stream()
                .toList();
    }

    public List<Page<CepEntity>> findByLocalidade(String localidade) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(localidade)
                .build());
        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder().queryConditional(queryConditional).build();

        var index = table.index("LocalidadeIndex");
        return index.query(queryEnhancedRequest).stream().toList();
    }


}
