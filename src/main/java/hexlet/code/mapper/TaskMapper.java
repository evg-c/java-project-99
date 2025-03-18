package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class, TaskStatusRepository.class },
        imports = { TaskStatusRepository.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    protected TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JsonNullableMapper jsonNullableMapper;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(
            target = "taskStatus",
            expression = "java(defineStatusFromCreateDTO(dto))"
    )
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(
            target = "labels",
            expression = "java(defineListLabelFromCreateDTO(dto))"
    )
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(
            target = "taskLabelIds",
            expression = "java(defineListIds(model.getLabels()))"
    )
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(
            target = "taskStatus",
            expression = "java(defineStatusFromUpdateDTO(dto, model))"
    )
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(
            target = "labels",
            expression = "java(defineListLabelFromUpdateDTO(dto, model))"
    )
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

//    @BeforeMapping
//    public void defineStatusIdFromCreate(TaskCreateDTO dto) {
//        var statusString = dto.getStatus();
//        //подразумевается, что мы выбираем статус задачи из таблицы статусов, иначе null
//        var taskStatus = taskStatusRepository.findBySlug(statusString).orElse(null);
//        var statusId = taskStatus == null ? null : taskStatus.getId();
//        dto.setStatusId(statusId);
//        List<Long> labelsIds = dto.getTaskLabelIds();
//        if (labelsIds != null) {
//            List<Label> labelList = new ArrayList<>();
//            for (Long labelId: labelsIds) {
//                //подразумевается, что мы выбираем метку из таблицы меток, иначе null
//                Label label = labelRepository.findById(labelId).orElse(null);
//                if (label != null) {
//                    labelList.add(label);
//                }
//            }
//            dto.setLabels(labelList);
//        }
//    }
//
    /**
    * Метод определения списка меток по списку их идентификаторов.
    * @param labelIds - список идентификаторов меток.
    * @return - список меток (объектов типа Label)
    * */
    public List<Label> defineListLabel(List<Long> labelIds) {
        if (labelIds == null) {
            return null;
        }
        List<Label> result = new ArrayList<>();
        for (Long idLabel: labelIds) {
            if (idLabel != null) {
                Label currentLabel = labelRepository.findById(idLabel).orElse(null);
                if (currentLabel != null) {
                    result.add(currentLabel);
                }
            }
        }
        return result;
    }

    /**
     * Метод определения списка меток по списку их идентификаторов из TaskUpdateDTO.
     * @param data - редактируемая задача в формате TaskUpdateDTO.
     * @param model - редактируемая задача в формате Task.
     * @return - список меток (объектов типа Label)
     * */
    public List<Label> defineListLabelFromUpdateDTO(TaskUpdateDTO data, Task model) {
        List<Label> oldListLabel = model.getLabels();
        boolean replace = jsonNullableMapper.isPresent(data.getTaskLabelIds());
        if (!replace) {
            return oldListLabel;
        }
        if (data == null) {
            return null;
        }
        JsonNullable<List<Long>> labelIdsJson = data.getTaskLabelIds();
        if (labelIdsJson == null) {
            return null;
        }
        List<Long> labelIds = labelIdsJson.get();
        if (labelIds == null) {
            return null;
        }
        return defineListLabel(labelIds);
    }

    /**
     * Метод определения списка меток по списку их идентификаторов из TaskCreateDTO.
     * @param data - редактируемая задача в формате TaskCreateDTO.
     * @return - список меток (объектов типа Label)
     * */
    public List<Label> defineListLabelFromCreateDTO(TaskCreateDTO data) {
        if (data == null) {
            return null;
        }
        List<Long> labelIds = data.getTaskLabelIds();
        if (labelIds == null) {
            return null;
        }
        return defineListLabel(labelIds);
    }

    /**
     * Метод определения списка меток по списку их идентификаторов.
     * @param listIds - список идентификаторов меток.
     * @return - список меток (объектов типа Label)
     * */
    public List<Label> defineListLabelFromListIds(List<Long> listIds) {
        List<Label> result = new ArrayList<>();
        for (Long idLabel: listIds) {
            if (idLabel != null) {
                Label currentLabel = labelRepository.findById(idLabel).orElse(null);
                if (currentLabel != null) {
                    result.add(currentLabel);
                }
            }
        }
        return result;
    }

    /**
     * Метод определения списка идентификаторов меток по списку самих меток.
     * @param labelList - список меток.
     * @return - список идентификаторов меток
     * */
    public List<Long> defineListIds(List<Label> labelList) {
        if (labelList == null) {
            return null;
        }
        List<Long> result = new ArrayList<>();
        for (Label currentLabel: labelList) {
            if (currentLabel != null) {
                result.add(currentLabel.getId());
            }
        }
        return result;
    }

    /**
     * Метод определения статуса по слагу из TaskUpdateDTO.
     * @param data - редактируемая задача в формате TaskUpdateDTO.
     * @param model - редактируемая задача в формате Task.
     * @return - статус (объект типа TaskStatus)
     * */
    public TaskStatus defineStatusFromUpdateDTO(TaskUpdateDTO data, Task model) {
        TaskStatus oldStatus = model.getTaskStatus();
        boolean replace = jsonNullableMapper.isPresent(data.getStatus());
        if (!replace) {
            return oldStatus;
        }
        if (data == null) {
            return null;
        }
        JsonNullable<String> statusJson = data.getStatus();
        if (statusJson == null) {
            return null;
        }
        String status = statusJson.get();
        if (status == null) {
            return null;
        }
        return defineStatus(status);
    }

    /**
     * Метод определения статуса по слагу из TaskCreateDTO.
     * @param data - редактируемая задача в формате TaskCreateDTO.
     * @return - статус (объект типа TaskStatus)
     * */
    public TaskStatus defineStatusFromCreateDTO(TaskCreateDTO data) {
        if (data == null) {
            return null;
        }
        String status = data.getStatus();
        if (status == null) {
            return null;
        }
        return defineStatus(status);
    }

    /**
     * Метод определения статуса по строке.
     * @param status - строковое представление.
     * @return - статус (объект типа TaskStatus)
     * */
    public TaskStatus defineStatus(String status) {
        if (status == null) {
            return null;
        }
        if (status.isEmpty()) {
            return null;
        }
        if (taskStatusRepository.findBySlug(status).isPresent()) {
            return taskStatusRepository.findBySlug(status).get();
        }
        return null;
    }
}
