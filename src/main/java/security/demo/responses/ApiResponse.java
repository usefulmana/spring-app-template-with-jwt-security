package security.demo.responses;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private boolean success;
    private final String jwt;

    public ApiResponse(String jwt, boolean success) {
        this.success = success;
        this.jwt = jwt;
    }
}
