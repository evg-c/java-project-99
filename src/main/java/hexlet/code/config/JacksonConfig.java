package hexlet.code.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.format.DateTimeFormatter;

/**
 * Конфигурационный класс для подключения jackson-databind-nullable к Jackson.
 */
@Configuration
public class JacksonConfig {

    /**
     * Метод настраивает ObjectMapper для сериализации объектов в JSON с использованием библиотеки Jackson.
     * @return - возвращает объект Jackson2ObjectMapperBuilder.
     */
    @Bean
    Jackson2ObjectMapperBuilder objectMapperBuilder() {
        var builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .modulesToInstall(new JsonNullableModule());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        builder.serializers(new LocalDateSerializer(formatter));

        return builder;
    }
}
