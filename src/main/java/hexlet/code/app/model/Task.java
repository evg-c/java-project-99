package hexlet.code.app.model;

import jakarta.persistence.*;
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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TaskStatus taskStatus;

    @ManyToOne
    private User assignee;

    @CreatedDate
    private LocalDate createdAt;
}
