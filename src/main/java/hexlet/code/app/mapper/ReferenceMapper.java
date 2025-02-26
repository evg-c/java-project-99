package hexlet.code.app.mapper;

import hexlet.code.app.model.BaseEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Класс используется для преобразования идентификатора сущности в саму сущность с помощью JPA EntityManager.
 */

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)

public abstract class ReferenceMapper {

    @Autowired
    private EntityManager entityManager;
    /**
     * Метод toEntity принимает идентификатор сущности и класс сущности, который нужно найти,
     * и возвращает найденную сущность или null, если идентификатор равен null.
     * @param id - идентификатор сущности.
     * @param <T> - класс сущности.
     * BaseEntity - Общий базовый интерфейс для всех моделей, на который маппер (ReferenceMapper) мог бы
     * ориентироваться и понимать, применять метод toEntity (ReferenceMapper.toEntity).
     * @param entityClass - на вход подается класс, в свойство которого конвертируем
     * @return - возвращает найденную сущность или null, если идентификатор равен null..
     */
    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        return id != null ? entityManager.find(entityClass, id) : null;
    }
}
