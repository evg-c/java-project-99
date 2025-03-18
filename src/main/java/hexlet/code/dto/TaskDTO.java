package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private Long index;

    private String title;

    private String content;

    private LocalDate createdAt;

    private String status;

    @JsonProperty("assignee_id")
    private Long assigneeId;
    //private Long assignee_id;

    private List<Long> taskLabelIds;
}
