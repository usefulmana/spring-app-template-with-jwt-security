package security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import security.demo.models.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUser_Id(Long id);
    Optional<VerificationToken> findByToken(String token);
}
