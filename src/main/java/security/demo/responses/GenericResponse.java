package security.demo.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericResponse {
    private boolean success;
    private String message;

    public GenericResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
