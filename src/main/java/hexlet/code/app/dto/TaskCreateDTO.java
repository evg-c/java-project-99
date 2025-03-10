package hexlet.code.app.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {

    private Long index;

    @Size(min = 1)
    private String title;

    private String content;

    @NotNull
    private String status;

    private Long statusId;

    private Long assigneeId;
}
