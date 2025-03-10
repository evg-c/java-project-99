package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Task implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long index;

    @Column(name = "title")
    @Size(min = 1)
    private String name;

    @Column(name = "content", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @ManyToOne(targetEntity = TaskStatus.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "status", referencedColumnName = "slug", nullable = false)
    private TaskStatus taskStatus;

    @ManyToOne(targetEntity = User.class, optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", referencedColumnName = "id", nullable = true)
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;
}
