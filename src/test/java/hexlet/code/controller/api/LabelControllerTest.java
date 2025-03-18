package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private Faker faker;

    private Label testLabel;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    /**
     * Метод начальной инициализации.
     */
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
        token = jwt().jwt(builder -> builder.subject(testLabel.getName()));
    }

    @Test
    @DisplayName("просмотр списка меток")
    public void testIndexOfLabels() throws Exception {
        var request = get("/api/labels");
        var response = mockMvc.perform(request.with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        List<LabelDTO> labelDTOS = objectMapper.readValue(bodyResponse, new TypeReference<>() { });
        var actual = labelDTOS;
        var expected = labelRepository.findAll().stream()
                .map(l -> labelMapper.map(l))
                .toList();
        assertThat(labelDtoToString(actual)).isEqualTo(labelDtoToString(expected));
    }

    /**
     * Метод перевода List<LabelDTO> в строку.
     * @param list - List<LabelDTO>, который надо перевести в строку.
     * @return - возвращает строковое представление
     */
    public String labelDtoToString(List<LabelDTO> list) {
        StringBuilder result = new StringBuilder();
        for (LabelDTO l: list) {
            result.append(" Label with id = ").append(l.getId())
                    .append(" : name = ").append(l.getName())
                    .append("\n");
        }
        return result.toString();
    }

    @Test
    @DisplayName("просмотр списка меток без аутентификации")
    public void testIndexOfLabelWithoutAuth() throws Exception {
        var request = get("/api/labels");
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    @DisplayName("просмотр одной метки")
    public void testShowLabel() throws Exception {
        var request = get("/api/labels/" + testLabel.getId()).with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThatJson(bodyResponse).and(
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    @DisplayName("просмотр одной метки без аутентификации")
    public void testShowLabelWithoutAuth() throws Exception {
        var request = get("/api/labels/" + testLabel.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    @DisplayName("создание новой метки")
    public void testCreateLabel() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();
        var requestBody = objectMapper.writeValueAsString(data);
        var request = post("/api/labels")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated());
        var label = labelRepository.findByName(data.getName()).get();
        assertNotNull(label);
        assertThat(label.getName()).isEqualTo(data.getName());
    }

    @Test
    @DisplayName("попытка создания новой метки без аутентификации")
    public void testCreateLabelWithoutAuth() throws Exception {
        var data = Instancio.of(modelGenerator.getLabelModel())
                .create();
        var requestBody = objectMapper.writeValueAsString(data);
        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    @DisplayName("редактирование метки")
    public void testUpdateLabel() throws Exception {
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("newLabel"));
        var requestBody = objectMapper.writeValueAsString(dto);
        var request = put("/api/labels/" + testLabel.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var label = labelRepository.findById(testLabel.getId()).get();
        assertThat(label.getName()).isEqualTo("newLabel");
    }

    @Test
    @DisplayName("редактирование метки без аутентификации")
    public void testUpdateLabelWithoutAuth() throws Exception {
        var dto = new LabelUpdateDTO();
        dto.setName(JsonNullable.of("newLabel"));
        var requestBody = objectMapper.writeValueAsString(dto);
        var request = put("/api/labels/" + testLabel.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    @DisplayName("удаление метки")
    public void testDeleteLabel() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(labelRepository.existsById(testLabel.getId())).isEqualTo(false);
    }

    @Test
    @DisplayName("удаление метки без аутентификации")
    public void testDeleteLabelWithoutAuth() throws Exception {
        var request = delete("/api/labels/" + testLabel.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }
}
