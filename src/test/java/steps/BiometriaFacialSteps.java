package steps;

import br.com.fiap.Challenge_Quod.ChallengeQuodApplication;
import br.com.fiap.Challenge_Quod.model.BiometriaFacial;
import br.com.fiap.Challenge_Quod.repository.BiometriaFacialRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = ChallengeQuodApplication.class)
@AutoConfigureMockMvc
public class BiometriaFacialSteps {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BiometriaFacialRepository biometriaFacialRepository;

    private MockMvc mockMvc;
    private MvcResult mvcResult;
    private MockMultipartFile imagemFile;
    private Map<String, Object> metadados = new HashMap<>();
    private BiometriaFacial.Dispositivo dispositivo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String jsonSchema;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        // Carrega o esquema JSON para validação
        Path schemaPath = Paths.get("src/test/resources/BiometriaFacial.schema.json");
        jsonSchema = Files.readString(schemaPath, StandardCharsets.UTF_8);
    }

    @After
    public void cleanup() {
        // Limpa os dados de teste após cada cenário
        metadados.clear();
        dispositivo = null;
        imagemFile = null;
    }

    @Dado("que o serviço de validação de biometria está disponível")
    public void servicoValidacaoBiometriaDisponivel() {
        // Não precisa implementar nada aqui, pois o serviço já está disponível via Spring Boot Test
    }

    @Dado("que tenho uma imagem válida para validação facial")
    public void tenhoImagemValidaParaValidacao() throws IOException {
        // Cria uma imagem de teste válida
        byte[] conteudoImagem = Files.readAllBytes(Paths.get("src/test/resources/imagens/face_valida.jpg"));
        imagemFile = new MockMultipartFile(
                "imagem",
                "face_valida.jpg",
                "image/jpeg",
                conteudoImagem
        );
    }

    @Dado("que tenho uma imagem inválida para validação facial")
    public void tenhoImagemInvalidaParaValidacao() {
        // Cria um arquivo que não é uma imagem
        byte[] conteudoTexto = "Este não é um arquivo de imagem".getBytes();
        imagemFile = new MockMultipartFile(
                "imagem",
                "arquivo.txt",
                "text/plain",
                conteudoTexto
        );
    }

    @Dado("que tenho uma imagem maior que 5MB para validação facial")
    public void tenhoImagemMuitoGrandeParaValidacao() throws IOException {
        // Cria ou carrega uma imagem grande para teste
        byte[] conteudoGrande = new byte[6 * 1024 * 1024]; // 6MB
        imagemFile = new MockMultipartFile(
                "imagem",
                "imagem_grande.jpg",
                "image/jpeg",
                conteudoGrande
        );
    }

    @E("tenho os metadados necessários para validação")
    public void tenhoMetadadosNecessariosParaValidacao(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String chave = row.get("chave");
            String valorString = row.get("valor");

            // Tentar converter o valor para tipos apropriados
            if (valorString.contains(".")) {
                try {
                    double valorDouble = Double.parseDouble(valorString);
                    metadados.put(chave, valorDouble);
                    continue;
                } catch (NumberFormatException ignored) {}
            }

            try {
                long valorLong = Long.parseLong(valorString);
                metadados.put(chave, valorLong);
                continue;
            } catch (NumberFormatException ignored) {}

            // Se não conseguir converter, mantém como string
            metadados.put(chave, valorString);
        }
    }

    @E("tenho os metadados incompletos para validação")
    public void tenhoMetadadosIncompletosParaValidacao(DataTable dataTable) {
        tenhoMetadadosNecessariosParaValidacao(dataTable);
        // Já está incompleto pelo próprio dataTable passado no teste
    }

    @E("tenho os dados do dispositivo")
    public void tenhoDadosDoDispositivo(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        Map<String, String> row = rows.get(0);

        dispositivo = new BiometriaFacial.Dispositivo(
                row.get("fabricante"),
                row.get("modelo"),
                row.get("sistemaOperacional")
        );
    }

    @E("não tenho os dados do dispositivo")
    public void naoTenhoDadosDoDispositivo() {
        dispositivo = null;
    }

    @Quando("solicito a validação da biometria facial")
    public void solicitoValidacaoBiometriaFacial() throws Exception {
        // Converte os metadados para JSON
        String metadadosJson = objectMapper.writeValueAsString(metadados);

        // Prepara os dados do dispositivo ou nulo
        String dispositivoJson = (dispositivo != null)
                ? objectMapper.writeValueAsString(dispositivo)
                : "{}";

        // Cria a parte JSON do request
        MockMultipartFile dadosRequest = new MockMultipartFile(
                "dados",
                "dados.json",
                "application/json",
                ("{\"metadados\":" + metadadosJson + ",\"dispositivo\":" + dispositivoJson + "}").getBytes()
        );

        // Executa a requisição
        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.multipart("/api/validar/biometria-facial")
                        .file(imagemFile)
                        .file(dadosRequest)
        ).andReturn();
    }

    @Dado("que tenho uma transação de biometria registrada com ID {string}")
    public void tenhoTransacaoBiometriaRegistrada(String transacaoIdStr) {
        UUID transacaoId = UUID.fromString(transacaoIdStr);

        // Cria uma biometria de teste e salva no repositório
        BiometriaFacial biometria = BiometriaFacial.builder()
                .transacaoId(transacaoId)
                .tipoBiometria("facial")
                .fraudeDetectada(false)
                .tipoFraude(null)
                .dataCaptura(Instant.now())
                .dispositivo(new BiometriaFacial.Dispositivo("Samsung", "Galaxy S23", "Android 14"))
                .metadados(Map.of("teste", "valor"))
                .imagem(new byte[100])
                .build();

        biometriaFacialRepository.save(biometria);
    }

    @Quando("consulto a biometria por este ID de transação")
    public void consultoTransacaoPorId() throws Exception {
        mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/validar/transacao/{id}", "550e8400-e29b-41d4-a716-446655440000")
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn();
    }

    @Então("a resposta deve ter o status {int}")
    public void respostaDeveTerStatus(int statusEsperado) {
        assertEquals(statusEsperado, mvcResult.getResponse().getStatus());
    }

    @E("o resultado deve conter um ID de transação válido")
    public void resultadoDeveConterIdTransacaoValido() throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Verifica se há um transacaoId e se é um UUID válido
        assertTrue(jsonNode.has("transacaoId"));
        String transacaoId = jsonNode.get("transacaoId").asText();

        try {
            UUID.fromString(transacaoId);
            // Se chegou aqui, é um UUID válido
        } catch (IllegalArgumentException e) {
            Assertions.fail("O campo transacaoId não contém um UUID válido");
        }
    }

    @E("o resultado deve ter o tipo de biometria {string}")
    public void resultadoDeveTerTipoBiometria(String tipoBiometriaEsperado) throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertTrue(jsonNode.has("tipoBiometria"));
        assertEquals(tipoBiometriaEsperado, jsonNode.get("tipoBiometria").asText());
    }

    @E("a mensagem de erro deve conter {string}")
    public void mensagemErroDeveConter(String mensagemErroEsperada) throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertTrue(responseBody.contains(mensagemErroEsperada));
    }

    @E("o resultado deve conter o mesmo ID de transação")
    public void resultadoDeveConterMesmoIdTransacao() throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertTrue(jsonNode.has("transacaoId"));
        assertEquals("550e8400-e29b-41d4-a716-446655440000", jsonNode.get("transacaoId").asText());
    }

    @E("a resposta deve estar de acordo com o esquema definido")
    public void respostaDeveEstarDeAcordoComEsquema() throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString();

        // Usar a biblioteca de validação JSON Schema aqui
        // Exemplo usando Everit JSON Schema
        org.everit.json.schema.Schema schema = org.everit.json.schema.loader.SchemaLoader.load(
                new org.json.JSONObject(jsonSchema)
        );

        schema.validate(new org.json.JSONObject(responseBody));
        // Se não lançar exceção, então está válido de acordo com o esquema
    }
}