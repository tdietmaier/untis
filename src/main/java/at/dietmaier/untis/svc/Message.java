package at.dietmaier.untis.svc;

import lombok.Data;

import javax.validation.constraints.Size;

/** REST interface message, also used as a business object */
@Data
public class Message {
    private long id;
    @Size(max=200)
    private String text;
}
