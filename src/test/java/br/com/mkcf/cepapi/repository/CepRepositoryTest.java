package br.com.mkcf.cepapi.repository;

import br.com.mkcf.cepapi.model.entity.CepEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepRepositoryTest {

    @Mock
    private DynamoDbEnhancedClient enhancedClient;

    @Mock
    private DynamoDbTable<CepEntity> table;;

    @Mock
    private DynamoDbIndex<CepEntity> index;

    private CepRepository cepRepository;

    @BeforeEach
    void setUp() {
        when(enhancedClient.table("ConsultaCEP",  TableSchema.fromBean(CepEntity.class))).thenReturn(table);
        cepRepository = new CepRepository(enhancedClient);
    }

    @Test
    void save_DeveSalvarCepEntity() {
        // Arrange
        CepEntity cepEntity = new CepEntity();
        cepEntity.setCep("12345678");
        cepEntity.setLogradouro("Rua Exemplo");
        doNothing().when(table).putItem(any(CepEntity.class));

        // Act
        cepRepository.save(cepEntity);

        // Assert
        verify(table, times(1)).putItem(cepEntity);
    }

    @Test
    void testFindByIdWithPartitionAndSortKey() {
        String cep = "12345-678";
        String dataCadastrado = "2023-10-01";
        CepEntity expectedEntity = new CepEntity();

        Key key = Key.builder().partitionValue(cep).sortValue(dataCadastrado).build();
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder().key(key).build();

        when(table.getItem(request)).thenReturn(expectedEntity);

        CepEntity result = cepRepository.findById(cep, dataCadastrado);

        // Verify the result and interactions
        assertEquals(expectedEntity, result);
        verify(table).getItem(request);
    }

    @Test
    void testFindByIdWithPartitionKeyOnly() {
        String cep = "12345-678";
        CepEntity entity1 = new CepEntity();
        CepEntity entity2 = new CepEntity();
        SdkIterable<CepEntity> iterable = mock(SdkIterable.class);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(cep).build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(queryConditional).build();

        when(table.query(request)).thenReturn(mock(PageIterable.class));
        when(table.query(request).items()).thenReturn(mock(SdkIterable.class));
        when(table.query(request).items().stream()).thenReturn(mock(Stream.class));
        when(table.query(request).items().stream().toList()).thenReturn(List.of(entity1, entity2));

        List<CepEntity> result = cepRepository.findById(cep);

        // Verify the result and interactions
        assertEquals(2, result.size());
        assertTrue(result.contains(entity1));
        assertTrue(result.contains(entity2));
    }


    @Test
    void testFindByLocalidade() {
        String localidade = "São Paulo";
        Page<CepEntity> page1 = mock(Page.class);
        Page<CepEntity> page2 = mock(Page.class);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(localidade).build());
        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(queryConditional).build();

        when(table.index("LocalidadeIndex")).thenReturn(index);

        SdkIterable<Page<CepEntity>> queryResult = mock(SdkIterable.class);
        when(index.query(request)).thenReturn(queryResult);
        when(index.query(request).stream()).thenReturn(Stream.of(page1, page2));

        List<Page<CepEntity>> result = cepRepository.findByLocalidade(localidade);

        // Verify the result and interactions
        assertEquals(2, result.size());
        assertTrue(result.contains(page1));
        assertTrue(result.contains(page2));
    }

}