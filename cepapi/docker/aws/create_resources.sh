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


echo -e "Imprimindo todos os atributos da fila sqs "
aws --endpoint-url=http://localhost:4566 --region=sa-east-1 sqs get-queue-attributes --queue-url $QueueUrlCorreios --attribute-names All


echo -e "Publicando uma msg de Teste no Topico 'itoken-async-enablement' SNS"
aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url $QueueUrlCorreios --message-body '{
  "cep": "38408-072",
  "logradouro": "Rua Romeu Margonari",
  "complemento": "",
  "unidade": "",
  "bairro": "Santa Mônica",
  "localidade": "Uberlândia",
  "uf": "MG",
  "estado": "Minas Gerais",
  "regiao": "Sudeste",
  "ibge": "3170206",
  "gia": "",
  "ddd": "34",
  "siafi": "5403",
  "timestamp": "'"$CURRENT_DATETIME"'"
}'


echo -e "Recebendo Msg da fila SQS"
aws --endpoint-url=http://localhost:4566 sqs receive-message \
  --queue-url $QueueUrlCorreios \
  --region sa-east-1 \
  --max-number-of-messages 1 \
  --wait-time-seconds 10

set +x