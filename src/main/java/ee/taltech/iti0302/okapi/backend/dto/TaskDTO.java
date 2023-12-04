package ee.taltech.iti0302.okapi.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class TaskDTO {
    private Long id;

    @NotBlank
    private Long customerId;

    @NotBlank
    private String title;
    private String description;
    private String status;
}
