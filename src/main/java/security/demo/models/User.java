package security.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String userName;

    @Column
    @Email
    private String email;

    @Column
    private String password;

    @Column
    private boolean isActive = false;

    @Column
    private String roles;

    @Column
    @CreatedDate
    private Date createdAt;

    @Column
    @LastModifiedDate
    private Date updatedAt;
}
