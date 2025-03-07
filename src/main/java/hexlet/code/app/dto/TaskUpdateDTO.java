package hexlet.code.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {

    JsonNullable<Long> index;

    @Size(min = 1)
    JsonNullable<String> title;

    JsonNullable<String> content;

    @NotNull
    JsonNullable<String> status;

    JsonNullable<Long> assignee_id;
}
