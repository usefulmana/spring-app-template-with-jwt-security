package security.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "validation_tokens")
@Data
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;


    @Column(name = "issued_date")
    private LocalDateTime issuedDateTime;

    @Column(name = "expired_date")
    private LocalDateTime expiredDateTime;

    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDateTime;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public VerificationToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.issuedDateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.expiredDateTime = issuedDateTime.plusHours(12);
        // Expire in 12 hours
    }
}
