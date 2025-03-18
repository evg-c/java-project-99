package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {

    JsonNullable<Long> index;

    @Size(min = 1)
    JsonNullable<String> title;

    JsonNullable<String> content;

    @NotNull
    JsonNullable<String> status;

    @JsonProperty("assignee_id")
    JsonNullable<Long> assigneeId;
    //JsonNullable<Long> assignee_id;

    JsonNullable<List<Long>> taskLabelIds;
}
