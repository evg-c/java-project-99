package hexlet.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelCreateDTO {

    @Column(unique = true)
    @Size(min = 3, max = 1000)
    private String name;
}
