package hexlet.code.app.mapper;

import hexlet.code.app.model.BaseEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class ReferenceMapper {

    @Autowired
    private EntityManager entityManager;
    /**
     * Метод для конвертации свойства из DTO в свойство внутри модели.
     * @param id - идентификатор свойства, из которого конвертируем
     * @param <T> - тип, в который конвертируем
     * @param entityClass - на вход подается класс, в свойство которого конвертируем
     * @return - возвращает свойство (объект) внутри модели.
     */
    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}
