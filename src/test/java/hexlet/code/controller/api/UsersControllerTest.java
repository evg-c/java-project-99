package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
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
public class UsersControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User testUser;

    /**
     * Метод начальной инициализации.
     */
    @BeforeEach
    public void setUp() {
        //userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void testIndexOfUsers() throws Exception {
        var response = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<UserDTO> userDTOS = objectMapper.readValue(body, new TypeReference<>() { });
        var actual = userDTOS.stream()
                //.sorted()
                .toList();
        var expected = userRepository.findAll().stream()
                .map(u -> userMapper.map(u))
                //.sorted()
                .toList();
        assertThat(userDtoToString(actual)).isEqualTo(userDtoToString(expected));
    }

    @Test
    public void testIndexOfUsersWithoutAuth() throws Exception {
        var response = mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThat(body).isEmpty();
    }

    /**
     * Метод перевода List<UserDTO> в строку.
     * @param list - List<UserDTO>, который надо перевести в строку.
     * @return - возвращает строковое представление
     */
    public String userDtoToString(List<UserDTO> list) {
        StringBuilder result = new StringBuilder();
        for (var u: list) {
            result.append(" User with id = ").append(u.getId())
                    .append(" : firstName = ").append(u.getFirstName())
                    .append(", lastName = ").append(u.getLastName())
                    .append(", email = ").append(u.getEmail())
                    .append("\n");
        }
        return result.toString();
    }

    @Test
    public void testShowUser() throws Exception {
        var request = get("/api/users/" + testUser.getId()).with(jwt());
        var response = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("email").isEqualTo(testUser.getEmail())
        );
    }

    @Test
    public void testShowUserWithoutAuth() throws Exception {
        var request = get("/api/users/" + testUser.getId());
        var response = mockMvc.perform(request)
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThat(body).isEmpty();
    }

    @Test
    public void testCreateUser() throws Exception {
        var data = Instancio.of(modelGenerator.getUserModel())
                .create();

        var request = post("/api/users")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        var user = userRepository.findByEmail(data.getEmail()).orElse(null);

        assertNotNull(user);
        assertThat(user.getFirstName()).isEqualTo(data.getFirstName());
        assertThat(user.getEmail()).isEqualTo(data.getEmail());
        assertThat(user.getPassword()).isNotEqualTo(data.getPassword());
    }

    @Test
    public void testUpdateUser() throws Exception {
        //userRepository.save(testUser);
        var dto = new UserUpdateDTO();
        dto.setFirstName(JsonNullable.of("Sam"));
        var request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var user = userRepository.findByEmail(testUser.getEmail()).orElseThrow();
        assertThat(user.getFirstName()).isEqualTo("Sam");
    }

    @Test
    public void testDeleteUser() throws Exception {
        var request = delete("/api/users/" + testUser.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(userRepository.existsById(testUser.getId())).isEqualTo(false);
    }
}
