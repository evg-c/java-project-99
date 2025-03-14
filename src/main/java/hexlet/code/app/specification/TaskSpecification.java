package hexlet.code.app.specification;

import hexlet.code.app.dto.TaskParamsDTO;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.model.Task;
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
//        return ((root, query, criteriaBuilder) -> {
//
//            CriteriaQuery<Task> crTask = criteriaBuilder.createQuery(Task.class);
//            CriteriaQuery<Label> crLabel = criteriaBuilder.createQuery(Label.class);
//            Root<Label> rootLabel = crLabel.from(Label.class);
//
//            // запрос по выборке Label, у которых id = labelId
//            var select1 = crLabel.select(rootLabel).where(criteriaBuilder.equal(rootLabel.get("id"), labelId));
//
//            // запрос по выборке Task, у которых labels входит в вышеуказанный запрос
//            var select2 = crTask.select(root).where(root.get("labels").in(select1));
//
//            // итоговый запрос
//            query = select2;
//
//            var result = query.subquery(Task.class);
//            return (labelId == null ?
//                    criteriaBuilder.conjunction() :
//                    criteriaBuilder.exists(query.subquery(Task.class)));
//                    //criteriaBuilder.all(query));
//        });

        return ((root, query, criteriaBuilder)
                -> labelId == null
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.equal(root.join("labels", JoinType.INNER).get("id"), labelId));
    }
}
