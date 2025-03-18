package hexlet.code.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskStatusCreateDTO {

    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String slug;
}
