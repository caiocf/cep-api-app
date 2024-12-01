#!/usr/bin/bash

echo -e "****************************"
echo -e "* Criando Recurso AWS      *"
echo -e "****************************"

export no_proxy="localhost"

echo "Criando fila SQS"

echo -e "Cria a fila DLQ"
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name Correios_DLQ --region sa-east-1

echo -e "Cria a fila principal e configura a DLQ"
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name Correios_SQS --region sa-east-1 --attributes '{
    "RedrivePolicy": "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\"arn:aws:sqs:sa-east-1:000000000000:Correios_DLQ\"}"
}'

# Definindo a data e hora atual no formato ISO 8601
CURRENT_DATETIME=$(date -u +"%Y-%m-%dT%H:%M:%S.%3NZ")

echo -e "Publicando a mensagem JSON na fila Correios_SQS"
aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url http://localhost:4566/000000000000/Correios_SQS --message-body '{
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
  --queue-url http://localhost:4566/000000000000/Correios_SQS \
  --region sa-east-1 \
  --max-number-of-messages 1 \
  --wait-time-seconds 10