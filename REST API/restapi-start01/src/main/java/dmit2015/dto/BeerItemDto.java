package dmit2015.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerItemDto implements Serializable {

    private Long id;

    @NotBlank(message = "Name value cannot be blank.")
    private String name;
    @NotBlank(message = "style value cannot be blank.")
    private String style;
    @NotBlank(message = "brand value cannot be blank.")
    private String brand;

    private boolean complete;

    private Integer version;

}