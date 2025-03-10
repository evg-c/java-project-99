package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import hexlet.code.app.service.TaskStatusService;
import hexlet.code.app.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusService taskStatusService;

    private Task testTask;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
    private User testUser;
    private TaskStatus testTaskStatus;

    /**
     * Метод начальной инициализации.
     */
    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

//        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
//        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
//        testUser = Instancio.of(modelGenerator.getUserModel()).create();

        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password(3, 100))
                .create();
        testTaskStatus = Instancio.of(TaskStatus.class)
                .ignore(Select.field(TaskStatus::getId))
                .supply(Select.field(TaskStatus::getName), () -> faker.name().name())
                .supply(Select.field(TaskStatus::getSlug), () -> faker.internet().slug())
                .create();
        testTask = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .supply(Select.field(Task::getIndex), () -> faker.number().randomNumber())
                .supply(Select.field(Task::getName), () -> faker.name().name())
                .supply(Select.field(Task::getDescription), () -> faker.gameOfThrones().quote())
                .supply(Select.field(Task::getTaskStatus), () -> testTaskStatus)
                .supply(Select.field(Task::getAssignee), () -> testUser)
                .create();
        userRepository.save(testUser);
        taskStatusRepository.save(testTaskStatus);
        taskRepository.save(testTask);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testIndexOfTasks() throws Exception {
        var request = get("/api/tasks").with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        List<TaskDTO> dtos = objectMapper.readValue(bodyResponse,
                new TypeReference<List<TaskDTO>>() { });
        var actual = dtos.stream()
                .sorted()
                .toList();
        var expected = taskRepository.findAll().stream()
                .map(task -> taskMapper.map(task))
                .sorted()
                .toList();
        assertThat(taskDtoToString(actual)).isEqualTo(taskDtoToString(expected));
    }

    /**
     * Метод перевода List<TaskDTO> в строку.
     * @param list - List<TaskDTO>, который надо перевести в строку.
     * @return - возвращает строковое представление
     */
    public String taskDtoToString(List<TaskDTO> list) {
        StringBuilder result = new StringBuilder();
        for (var t: list) {
            result.append(" Task with id = ").append(t.getId())
                    .append(" : index = ").append(t.getIndex())
                    .append(", title = ").append(t.getTitle())
                    .append(", content = ").append(t.getContent())
                    .append(", status = ").append(t.getStatus())
                    .append(", assignee_id = ").append(t.getAssigneeId())
                    .append("\n");
        }
        return result.toString();
    }

    @Test
    public void testIndexOfTasksWithoutAuth() throws Exception {
        var request = get("/api/tasks");
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    public void testShowTask() throws Exception {
        var request = get("/api/tasks/" + testTask.getId()).with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThatJson(bodyResponse).and(
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug()),
                v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    public void testShowTaskWithoutAuth() throws Exception {
        var request = get("/api/tasks/" + testTask.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();
    }

    @Test
    public void testCreateTask() throws Exception {
        //var data = Instancio.of(modelGenerator.getTaskModel())
        //        .create();
        TaskCreateDTO newCreateTask = new TaskCreateDTO();
        newCreateTask.setIndex(101L);
        newCreateTask.setTitle("newTitle");
        newCreateTask.setContent("newContent");
        newCreateTask.setStatus("draft");
        newCreateTask.setAssigneeId(null);
        var requestBody = objectMapper.writeValueAsString(newCreateTask);
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var task = taskRepository.findByIndex(testTask.getIndex()).get();
        assertThat(task.getName()).isEqualTo(testTask.getName());
    }

    @Test
    public void testCreateWithoutAuth() throws Exception {
        TaskCreateDTO newCreateTask = new TaskCreateDTO();
        newCreateTask.setIndex(101L);
        newCreateTask.setTitle("newTitle");
        newCreateTask.setContent("newContent");
        newCreateTask.setStatus("draft");
        newCreateTask.setAssigneeId(null);
        var requestBody = objectMapper.writeValueAsString(newCreateTask);
        var request = post("/api/tasks")
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
    public void testUpdateTask() throws Exception {
        //taskRepository.save(testTask);
        //
//        TaskCreateDTO newCreateTask = new TaskCreateDTO();
//        newCreateTask.setIndex(101L);
//        newCreateTask.setTitle("newTitle");
//        newCreateTask.setContent("newContent");
//        newCreateTask.setStatus("draft");
//        newCreateTask.setAssignee_id(null);
//        var requestBody = objectMapper.writeValueAsString(newCreateTask);
//        var request = post("/api/tasks")
//                .with(token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody);
//        mockMvc.perform(request)
//                .andExpect(status().isCreated());
//        var task = taskRepository.findByIndex(101L).get();
//        assertThat(task.getName()).isEqualTo("newTitle");
        //
        var dto = new TaskUpdateDTO();
        dto.setIndex(JsonNullable.of(151L));
        dto.setTitle(JsonNullable.of("new-title"));
        dto.setContent(JsonNullable.of("new-content"));
        dto.setStatus(JsonNullable.of("draft"));
        var requestBodyUpdate = objectMapper.writeValueAsString(dto);
        var requestUpdate = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyUpdate);
        mockMvc.perform(requestUpdate)
                .andExpect(status().isOk());
        var updateTask = taskRepository.findByIndex(151L).get();
        assertThat(updateTask.getName()).isEqualTo("new-title");
        assertThat(updateTask.getDescription()).isEqualTo("new-content");
        assertThat(updateTask.getTaskStatus().getSlug()).isEqualTo("draft");
    }

    @Test
    public void testUpdateTaskWithoutAuth() throws Exception {
        //taskRepository.save(testTask);
        //
//        TaskCreateDTO newCreateTask = new TaskCreateDTO();
//        newCreateTask.setIndex(101L);
//        newCreateTask.setTitle("newTitle");
//        newCreateTask.setContent("newContent");
//        newCreateTask.setStatus("draft");
//        newCreateTask.setAssignee_id(null);
//        var requestBody = objectMapper.writeValueAsString(newCreateTask);
//        var request = post("/api/tasks")
//                .with(token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody);
//        mockMvc.perform(request)
//                .andExpect(status().isCreated());
//        var task = taskRepository.findByIndex(101L).get();
//        assertThat(task.getName()).isEqualTo("newTitle");
        //
        var dto = new TaskUpdateDTO();
        dto.setIndex(JsonNullable.of(151L));
        dto.setTitle(JsonNullable.of("new-title"));
        dto.setContent(JsonNullable.of("new-content"));
        dto.setStatus(JsonNullable.of("draft"));
        var requestBodyUpdate = objectMapper.writeValueAsString(dto);
        var requestUpdate = put("/api/tasks/" + testTask.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBodyUpdate);
        var response = mockMvc.perform(requestUpdate)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var bodyResponse = response.getContentAsString();
        assertThat(bodyResponse).isEmpty();

//        var updateTask = taskRepository.findByIndex(151L).get();
//        assertThat(updateTask.getName()).isEqualTo("new-title");
//        assertThat(updateTask.getDescription()).isEqualTo("new-content");
        //assertThat(task.getTaskStatus()).isEqualTo("draft");
    }

    @Test
    public void testDeleteTask() throws Exception {
        taskRepository.save(testTask);

        //
//        TaskCreateDTO newCreateTask = new TaskCreateDTO();
//        newCreateTask.setIndex(101L);
//        newCreateTask.setTitle("newTitle");
//        newCreateTask.setContent("newContent");
//        newCreateTask.setStatus("draft");
//        newCreateTask.setAssignee_id(null);
//        var requestBody = objectMapper.writeValueAsString(newCreateTask);
//        var request = post("/api/tasks")
//                .with(token)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody);
//        mockMvc.perform(request)
//                .andExpect(status().isCreated());
//        var task = taskRepository.findByIndex(101L).get();
//        assertThat(task.getName()).isEqualTo("newTitle");
//        //

        var requestDelete = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(requestDelete)
                .andExpect(status().isNoContent());
        assertThat(taskRepository.existsById(testTask.getId())).isEqualTo(false);
    }
}
