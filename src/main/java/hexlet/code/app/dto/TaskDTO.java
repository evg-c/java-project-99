package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskDTO {

    private Long id;

    private Long index;

    private String title;

    private String content;

    private LocalDate createdAt;

    private String status;

    //private Long statusId;

    private Long assigneeId;
}
