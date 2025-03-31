package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import jakarta.persistence.criteria.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TaskSpecification {

    @Autowired
    private TaskMapper taskMapper;

    /**
     * Метод сборки спецификации на основе переданных параметров.
     * @param params - параметры запроса в формате TaskParamsDTO
     * @return - возвращает объект для генерации данных.
     */
    public Specification<Task> build(TaskParamsDTO params) {
        return withAssigneeId(params.getAssigneeId())
                .and(withTitleCont(params.getTitleCont()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return ((root, query, criteriaBuilder)
                -> assigneeId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return ((root, query, criteriaBuilder)
                -> titleCont == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + titleCont.toLowerCase() + "%"));
    }

    private Specification<Task> withStatus(String status) {
        return ((root, query, criteriaBuilder)
                -> status == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.get("taskStatus").get("slug"), status));
    }

    private Specification<Task> withLabelId(Long labelId) {
        return ((root, query, criteriaBuilder)
                -> labelId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.join("labels", JoinType.INNER).get("id"), labelId));
    }
}
