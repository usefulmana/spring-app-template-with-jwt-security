package security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import security.demo.models.ValidationToken;

public interface ValidationTokenRepository extends JpaRepository<ValidationToken, Long> {

}
