package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskStatusRepository;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    //@Mapping(source = "status", target = "taskStatus.slug")
    @Mapping(source = "statusId", target = "taskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    //@Mapping(source = "status", target = "taskStatus.slug")
    @Mapping(source = "statusId", target = "taskStatus")
    @Mapping(source = "assigneeId", target = "assignee")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    /**
     * Метод определения идентификатора статуса.
     * @param dto - объект редактируемой задачи из PUT-запроса.
     */
    @BeforeMapping
    public void defineStatusIdFromUpdate(TaskUpdateDTO dto) {
        var statusString = dto.getStatus().get();
        //подразумевается, что мы выбираем статус задачи из таблицы статусов, иначе null
        var taskStatus = taskStatusRepository.findBySlug(statusString).orElse(null);
        var statusId = taskStatus == null ? null : taskStatus.getId();
        dto.setStatusId(JsonNullable.of(statusId));
    }

    /**
     * Метод определения идентификатора статуса.
     * @param dto - объект создаваемой задачи из POST-запроса.
     */
    @BeforeMapping
    public void defineStatusIdFromCreate(TaskCreateDTO dto) {
        var statusString = dto.getStatus();
        //подразумевается, что мы выбираем статус задачи из таблицы статусов, иначе null
        var taskStatus = taskStatusRepository.findBySlug(statusString).orElse(null);
        var statusId = taskStatus == null ? null : taskStatus.getId();
        dto.setStatusId(statusId);
    }
}
