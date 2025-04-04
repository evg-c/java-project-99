package hexlet.code.mapper;

import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.openapitools.jackson.nullable.JsonNullable;
/**
 * Маппер для обеспечения работы JsonNullable.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class JsonNullableMapper {

    /**
     * Метод для обработки свойств, которые могут быть отсутствующими (null), без непосредственной проверки на null,
     * предлагая более безопасный и удобный способ работы с такими значениями.
     * Метод wrap принимает объект типа T и оборачивает его в объект JsonNullable.
     * @param <T> - тип, из которого конвертируем.
     * @param entity - значение, которое обрабатываем.
     * @return - возвращает свойство, обернутое в JsonNullable.
     */
    public <T> JsonNullable<T> wrap(T entity) {
        return JsonNullable.of(entity);
    }

    /**
     * Обратный метод, безопасно извлекает значение из объекта JsonNullable, обрабатывая случай,
     * когда сам объект может быть null.
     * Метод unwrap принимает объект JsonNullable и возвращает его значение, либо null,
     * если объект JsonNullable равен null.
     * @param <T> - тип, из которого конвертируем.
     * @param jsonNullable - значение, которое обрабатываем.
     * @return - возвращает искомое свойство.
     */
    public <T> T unwrap(JsonNullable<T> jsonNullable) {
        return jsonNullable == null ? null : jsonNullable.orElse(null);
    }

    /**
     * Метод проверяет, содержится ли значение в объекте типа JsonNullable.
     * Метод isPresent проверяет, содержит ли объект JsonNullable значение.
     * @param <T> - на входе тип, который проверяем.
     * @param nullable - значение, которое проверяем.
     * @return - возвращает boolean результат.
     */
    @Condition
    public <T> boolean isPresent(JsonNullable<T> nullable) {
        return nullable != null && nullable.isPresent();
    }
}
