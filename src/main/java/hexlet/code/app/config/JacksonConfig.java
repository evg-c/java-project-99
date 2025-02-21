package hexlet.code.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Конфигурационный класс для подключения jackson-databind-nullable к jackson.
 */
@Configuration
public class JacksonConfig {

    /**
     * Метод для обеспечения работы jackson-databind-nullable.
     * @return - возвращает объект Jackson2ObjectMapperBuilder, определяющий, что в свойстве: null или значение
     */
    @Bean
    Jackson2ObjectMapperBuilder objectMapperBuilder() {
        var builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .modulesToInstall(new JsonNullableModule());
        return builder;
    }
}
