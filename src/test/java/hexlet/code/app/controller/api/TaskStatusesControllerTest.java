package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskStatusesControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskStatusMapper mapper;

    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Faker faker;

    private TaskStatus testTaskStatus;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private User testUser;

    /**
     * Метод начальной инициализации.
     */
    @BeforeEach
    public void setUp() {
        //repository.deleteAll();

        //testTaskStatus = Instancio.of(TaskStatus.class)
        //        .ignore(Select.field(TaskStatus::getId))
        //        .supply(Select.field(TaskStatus::getName), () -> faker.gameOfThrones().house())
        //        .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
        //        .create();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        userRepository.save(testUser);
        repository.save(testTaskStatus);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testIndexOfTaskStatus() throws Exception {
        //repository.save(testTaskStatus);
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<TaskStatusDTO> taskStatusDTOS = objectMapper.readValue(body, new TypeReference<>() { });
        var actual = taskStatusDTOS.stream()
                //.sorted(Comparator.comparingLong(t -> t.getId()))
                .toList();
        var expected = repository.findAll().stream()
                .map(u -> mapper.map(u))
                //.sorted(Comparator.comparingLong(t -> t.getId()))
                .toList();
        assertThat(taskStatusDtoToString(actual)).isEqualTo(taskStatusDtoToString(expected));
        //Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    /**
     * Метод перевода List<TaskStatusDTO> в строку.
     * @param list - List<TaskStatusDTO>, который надо перевести в строку.
     * @return - возвращает строковое представление
     */
    public String taskStatusDtoToString(List<TaskStatusDTO> list) {
        StringBuilder result = new StringBuilder();
        for (var ts: list) {
            result.append(" TaskStatus with id = ").append(ts.getId())
                    .append(" : name = ").append(ts.getName())
                    .append(", slug = ").append(ts.getSlug())
                    .append("\n");
        }
        return result.toString();
    }

    @Test
    public void testIndexOfTaskStatusWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThat(body).isEmpty();
    }

    @Test
    public void testShowTaskStatus() throws Exception {
        //repository.save(testTaskStatus);
        var request = get("/api/task_statuses/" + testTaskStatus.getId()).with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()),
                v -> v.node("slug").isEqualTo(testTaskStatus.getSlug())
        );
    }

    @Test
    public void testShowTaskStatusWithoutAuth() throws Exception {
        var request = get("/api/task_statuses/" + testTaskStatus.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThat(body).isEmpty();
    }

    @Test
    public void testCreateTaskStatus() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var taskStatus = repository.findBySlug(data.getSlug()).get();
        assertNotNull(taskStatus);
        assertThat(taskStatus.getName()).isEqualTo(data.getName());
    }

    @Test
    public void testCreateTaskStatusWithoutAuth() throws Exception {
        var data = Instancio.of(modelGenerator.getTaskStatusModel())
                .create();
        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var responseBody = response.getContentAsString();
        assertThat(responseBody).isEmpty();
    }

    @Test
    public void testUpdateTaskStatus() throws Exception {
        //repository.save(testTaskStatus);
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("new-status"));
        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var taskStatus = repository.findBySlug(testTaskStatus.getSlug()).get();
        assertThat(taskStatus.getName()).isEqualTo("new-status");
    }

    @Test
    public void testUpdateTaskStatusWithoutAuth() throws Exception {
        //repository.save(testTaskStatus);
        var dto = new TaskStatusUpdateDTO();
        dto.setName(JsonNullable.of("new-status"));
        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var responseBody = response.getContentAsString();
        assertThat(responseBody).isEmpty();
    }

    @Test
    public void testDeleteTaskStatus() throws Exception {
        //repository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(repository.existsById(testTaskStatus.getId())).
                isEqualTo(false);
    }

    @Test
    public void testDeleteTaskStatusWithoutAuth() throws Exception {
        //repository.save(testTaskStatus);
        var request = delete("/api/task_statuses/" + testTaskStatus.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var responseBody = response.getContentAsString();
        assertThat(responseBody).isEmpty();
    }
}
