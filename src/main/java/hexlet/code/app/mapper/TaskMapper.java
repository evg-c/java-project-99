package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
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
        imports = { hexlet.code.app.repository.TaskStatusRepository.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    protected TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(
            target = "taskStatus",
            expression = "java(taskStatusRepository.findBySlug(dto.getStatus()).orElse(null))"
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
            expression = "java(taskStatusRepository.findBySlug(dto.getStatus().get()).orElse(null))"
    )
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(
            target = "labels",
            expression = "java(defineListLabelFromUpdateDTO(dto))"
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
     * @return - список меток (объектов типа Label)
     * */
    public List<Label> defineListLabelFromUpdateDTO(TaskUpdateDTO data) {
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
}
