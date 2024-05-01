package dmit2015.restclient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeerItemRbac {

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
