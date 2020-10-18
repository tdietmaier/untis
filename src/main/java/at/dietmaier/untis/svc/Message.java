package at.dietmaier.untis.svc;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/** REST interface message, also used as a business object */
@Data
public class Message {
    @NotNull
    private Long id;

    @Size(max=200)
    @NotNull
    private String text;
}
