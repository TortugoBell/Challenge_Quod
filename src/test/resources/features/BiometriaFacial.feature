# language: pt

Funcionalidade: Validação de biometria facial
  Como um sistema de validação biométrica
  Eu quero verificar e validar imagens faciais
  Para detectar possíveis fraudes e garantir a identidade dos usuários

  Contexto:
    Dado que o serviço de validação de biometria está disponível

  Cenário: Validação de biometria facial com sucesso
    Dado que tenho uma imagem válida para validação facial
    E tenho os metadados necessários para validação
      | chave        | valor        |
      | latitude     | -23.5505     |
      | longitude    | -46.6333     |
      | timestamp    | 1715644800000|
      | confianca    | 0.95         |
    E tenho os dados do dispositivo
      | fabricante        | modelo      | sistemaOperacional |
      | Samsung           | Galaxy S23  | Android 14         |
    Quando solicito a validação da biometria facial
    Então a resposta deve ter o status 200
    E o resultado deve conter um ID de transação válido
    E o resultado deve ter o tipo de biometria "facial"
    E a resposta deve estar de acordo com o esquema definido

  Cenário: Validação com imagem inválida
    Dado que tenho uma imagem inválida para validação facial
    E tenho os metadados necessários para validação
      | chave        | valor        |
      | latitude     | -23.5505     |
      | longitude    | -46.6333     |
      | timestamp    | 1715644800000|
      | confianca    | 0.95         |
    E tenho os dados do dispositivo
      | fabricante        | modelo      | sistemaOperacional |
      | Apple             | iPhone 15   | iOS 18             |
    Quando solicito a validação da biometria facial
    Então a resposta deve ter o status 400
    E a mensagem de erro deve conter "não é uma imagem válida"

  Cenário: Validação com imagem muito grande
    Dado que tenho uma imagem maior que 5MB para validação facial
    E tenho os metadados necessários para validação
      | chave        | valor        |
      | latitude     | -23.5505     |
      | longitude    | -46.6333     |
      | timestamp    | 1715644800000|
      | confianca    | 0.95         |
    E tenho os dados do dispositivo
      | fabricante        | modelo      | sistemaOperacional |
      | Google            | Pixel 8     | Android 15         |
    Quando solicito a validação da biometria facial
    Então a resposta deve ter o status 400
    E a mensagem de erro deve conter "não pode exceder 5MB"

  Cenário: Validação sem metadados obrigatórios
    Dado que tenho uma imagem válida para validação facial
    E tenho os metadados incompletos para validação
      | chave        | valor        |
      | latitude     | -23.5505     |
    E tenho os dados do dispositivo
      | fabricante        | modelo      | sistemaOperacional |
      | Motorola          | Edge 40     | Android 14         |
    Quando solicito a validação da biometria facial
    Então a resposta deve ter o status 400
    E a mensagem de erro deve conter "Os metadados são obrigatórios"

  Cenário: Validação sem dados do dispositivo
    Dado que tenho uma imagem válida para validação facial
    E tenho os metadados necessários para validação
      | chave        | valor        |
      | latitude     | -23.5505     |
      | longitude    | -46.6333     |
      | timestamp    | 1715644800000|
      | confianca    | 0.95         |
    E não tenho os dados do dispositivo
    Quando solicito a validação da biometria facial
    Então a resposta deve ter o status 400
    E a mensagem de erro deve conter "Os dados do dispositivo são obrigatórios"

  Cenário: Consulta de biometria por ID de transação
    Dado que tenho uma transação de biometria registrada com ID "550e8400-e29b-41d4-a716-446655440000"
    Quando consulto a biometria por este ID de transação
    Então a resposta deve ter o status 200
    E o resultado deve conter o mesmo ID de transação
    E a resposta deve estar de acordo com o esquema definido