package hexlet.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {

    @Column(unique = true)
    @Size(min = 3, max = 1000)
    JsonNullable<String> name;
}
