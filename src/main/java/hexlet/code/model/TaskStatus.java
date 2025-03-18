package hexlet.code.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "task_statuses")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@ToString(includeFieldNames = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TaskStatus implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(unique = true)
    @Size(min = 1)
    private String name;

    @EqualsAndHashCode.Include
    @ToString.Include
    @Column(unique = true)
    @Size(min = 1)
    private String slug;

    @CreatedDate
    private LocalDate createdAt;

    @OneToMany(targetEntity = Task.class, mappedBy = "taskStatus", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Метод для обновления списка задач с данным статусом.
     * @param task - добавляемая задача с данным статусом
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setTaskStatus(this);
    }

    /**
     * Метод для обновления списка задач с данным статусом.
     * @param task - удаляемая задача с данным статусом
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setTaskStatus(null);
    }
}
