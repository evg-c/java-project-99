package hexlet.code.mapper;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;


@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    public abstract User map(UserCreateDTO dto);
    public abstract UserDTO map(User model);
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    /**
     * Метод хэширования пароля нового пользователя.
     * @param dto - объект создаваемого пользователя из POST-запроса.
     */
    @BeforeMapping
    public void encryptPassword(UserCreateDTO dto) {
        var password = dto.getPassword();
        dto.setPassword(passwordEncoder.encode(password));
    }

    /**
     * Метод хэширования редактируемого пароля.
     * @param dto - объект создаваемого пользователя из PUT-запроса.
     */
    @BeforeMapping
    public void encryptPassword(UserUpdateDTO dto) {
        String hashedPassword = null;
        if (jsonNullableMapper.isPresent(dto.getPassword())) {
            JsonNullable<String> passwordFromUpdateDTO = dto.getPassword();
            //var password  = passwordFromUpdateDTO.get();
            hashedPassword = passwordEncoder.encode(jsonNullableMapper.unwrap(dto.getPassword()));
            //hashedPassword = passwordEncoder.encode(password);
            dto.setPassword(JsonNullable.of(hashedPassword));
        }
    }
}
