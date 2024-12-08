#!/usr/bin/bash

echo -e "****************************"
echo -e "* Criando Recurso AWS      *"
echo -e "****************************"

export no_proxy="localhost"
NomeFila=Correios_SQS
NomeFilaDlq=Correios_SQS_DLQ

echo -e "INFO - Criado Fila SQS"
aws --endpoint-url=http://localhost:4566 sqs --region=sa-east-1 create-queue --queue-name $NomeFila
aws --endpoint-url=http://localhost:4566 sqs --region=sa-east-1 create-queue --queue-name $NomeFilaDlq

echo -e "Obter o QueueUrl das Filas"
QueueUrlCorreios=$(aws --endpoint-url=http://localhost:4566 sqs get-queue-url --queue-name "$NomeFila" --query 'QueueUrl' --output text)
QueueUrlCorreiosDlq=$(aws --endpoint-url=http://localhost:4566 sqs get-queue-url --queue-name "$NomeFilaDlq" --query 'QueueUrl' --output text)


echo -e "Obter o ARN da DLQ"
DLQ_ARN_CORREIOS=$(aws --endpoint-url=http://localhost:4566 --region=sa-east-1 sqs get-queue-attributes --queue-url $QueueUrlCorreiosDlq --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)


echo -e "Configurar a política de redrive"
aws --endpoint-url=http://localhost:4566 --region=sa-east-1 sqs set-queue-attributes --queue-url $QueueUrlCorreios --attributes '{"RedrivePolicy":"{\"deadLetterTargetArn\":\"'"$DLQ_ARN_CORREIOS"'\",\"maxReceiveCount\":\"5\"}"}'


echo -e "Imprimindo todos os atributos da fila sqs $QueueUrlCorreios "
aws --endpoint-url=http://localhost:4566 --region=sa-east-1 sqs get-queue-attributes --queue-url $QueueUrlCorreios --attribute-names All

# Definindo a data e hora atual no formato ISO 8601
CURRENT_DATETIME=$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")

#echo -e "Publicando uma msg de Teste no Topico $QueueUrlCorreios SNS"
#aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url $QueueUrlCorreios --message-body '
#{
#    "cep": {
#        "logradouro": "Romeu Margonari",
#        "bairro": "Santa Mônica",
#        "localidade": "Uberlândia",
#        "uf": "MG"
#    },
#    "codigoCep": "38408072"
#}'


#echo -e "Recebendo Msg da fila SQS $QueueUrlCorreios"
#aws --endpoint-url=http://localhost:4566 sqs receive-message \
#  --queue-url $QueueUrlCorreios \
#  --region sa-east-1 \
#  --max-number-of-messages 1 \
#  --wait-time-seconds 10

echo -e "#########################################################################"

echo -e "Criando a tabela Dynamodb"
aws dynamodb create-table \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --attribute-definitions \
        AttributeName=cep,AttributeType=S \
        AttributeName=localidade,AttributeType=S \
		AttributeName=dataCadastrado,AttributeType=S \
        AttributeName=uf,AttributeType=S \
    --key-schema \
        AttributeName=cep,KeyType=HASH \
        AttributeName=dataCadastrado,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --global-secondary-indexes \
        '[
            {
                "IndexName": "LocalidadeIndex",
                "KeySchema": [
                    {"AttributeName": "localidade", "KeyType": "HASH"}
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                },
                "ProvisionedThroughput": {
                    "ReadCapacityUnits": 5,
                    "WriteCapacityUnits": 5
                }
            },
            {
                "IndexName": "UfIndex",
                "KeySchema": [
                    {"AttributeName": "uf", "KeyType": "HASH"}
                ],
                "Projection": {
                    "ProjectionType": "ALL"
                },
                "ProvisionedThroughput": {
                    "ReadCapacityUnits": 5,
                    "WriteCapacityUnits": 5
                }
            }
        ]'

echo -e "Definindo o atributo expirationTimestamp como TTL"
aws dynamodb update-time-to-live \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --time-to-live-specification \
        "Enabled=true, AttributeName=expirationTimestamp"

echo -e "Listando as tabelas:"
aws --endpoint-url=http://localhost:4566 dynamodb list-tables

echo -e "Consulta Índice da tabela ConsultaCEP"
aws --endpoint-url http://localhost:4566 dynamodb describe-table --table-name ConsultaCEP

current_time=$(date +%s)
ttl_value=$((current_time + 2592000)) # Expiração em 30 dias (2592000 segundos)

echo -e "Cadastrando item no na tabela do ConsultaCEP"
aws --endpoint-url=http://localhost:4566 dynamodb put-item \
    --table-name ConsultaCEP \
    --item '{
        "cep": {"S": "38408072"},
		    "dataCadastrado": {"S": "2024-12-06T03:32:21.164888800Z"},
		    "expirationTimestamp": {"N": "1733280051"},
        "complemento": {"S": ""},
        "logradouro": {"S": "Rua Romeu Margonari"},
        "bairro": {"S": "Santa Mônica"},
        "localidade": {"S": "Uberlândia"},
        "uf": {"S": "MG"}
    }'

echo -e "Consulta uma pela Primary Key Condition expression"
#aws dynamodb get-item \
#    --endpoint-url http://localhost:4566 \
#    --table-name ConsultaCEP \
#    --key '{"cep": {"S": "38408-072"}}'
aws dynamodb query \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --key-condition-expression "cep = :cep" \
    --expression-attribute-values '{":cep": {"S": "38408072"}}'

echo -e "Consulta uma pela Primary Key e Sort Key com Condition expression"
aws dynamodb query \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --key-condition-expression "cep = :cep AND dataCadastrado = :ts" \
    --expression-attribute-values '{":cep": {"S": "38408072"}, ":ts": {"S": "2024-12-06T03:32:21.164888800Z"}}'

echo -e "Consulta uma pela uf pelo GSI UfIndex"
aws dynamodb query \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --index-name UfIndex \
    --key-condition-expression "uf = :uf" \
    --expression-attribute-values '{":uf": {"S": "MG"}}'

echo -e "Consulta uma pela uf pelo GSI LocalidadeIndex"
aws dynamodb query \
    --endpoint-url http://localhost:4566 \
    --table-name ConsultaCEP \
    --index-name LocalidadeIndex \
    --key-condition-expression "localidade = :loc" \
    --expression-attribute-values '{":loc": {"S": "Uberlândia"}}'


set +x