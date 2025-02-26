package hexlet.code.app.mapper;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
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

    public abstract User map(UserCreateDTO dto);
    public abstract UserDTO map(User model);
    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);

    /**
     * Метод хэширования пароля.
     * @param dto - объект создаваемого пользователя из POST-запроса.
     */
    @BeforeMapping
    public void encryptPassword(UserCreateDTO dto) {
        var password = dto.getPassword();
        dto.setPassword(passwordEncoder.encode(password));
    }
}
