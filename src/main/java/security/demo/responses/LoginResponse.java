package security.demo.responses;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private boolean success;
    private String message;
    private final String jwt;

    public LoginResponse(String jwt, boolean success, String message) {
        this.success = success;
        this.jwt = jwt;
        this.message = message;
    }
}
