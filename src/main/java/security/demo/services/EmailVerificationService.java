package security.demo.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import security.demo.exceptions.BadRequestException;
import security.demo.models.Mail;
import security.demo.models.User;
import security.demo.models.VerificationToken;
import security.demo.repositories.UserRepository;
import security.demo.repositories.VerificationTokenRepository;
import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailVerificationService {

    @Value("${spring.mail.username}")
    @Getter
    private String hostEmail;

    @Value("${host}")
    @Getter
    private String host;

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Async
    public void sendVerificationEmail(User user, VerificationToken token) throws IOException, MessagingException {

        if (token == null){
            token = generateToken(user);
        }

        Mail mail = new Mail();
        mail.setFrom(this.getHostEmail());
        mail.setTo(user.getEmail());
        mail.setSubject("[DO NOT REPLY] Complete Your Registration");

        Map<String, Object> model =  new HashMap<>();
        model.put("verification_url", String.format("%s/auth/confirm?token=%s", this.getHost(),
                token.getToken()));
        mail.setModel(model);
        emailService.sendSimpleMessage(mail);
    }

    public void resendToken(User user) throws IOException, MessagingException {
        Optional<VerificationToken> optionalToken = verificationTokenRepository.findByUser_Id(user.getId());
        if (user.isVerified()){
            throw new BadRequestException("This user is already verified");
        }

        if (optionalToken.isEmpty()){
            sendVerificationEmail(user, null);
        }
        else {
            VerificationToken token = optionalToken.get();
            if (LocalDateTime.now().isBefore(token.getExpiredDateTime())){
                sendVerificationEmail(user, optionalToken.get());
            }

            else if (LocalDateTime.now().isAfter(token.getExpiredDateTime())){
                verificationTokenRepository.delete(token);
                sendVerificationEmail(user, null);
            }
        }
    }

    public void verifyToken(String token){
        Optional<VerificationToken> tokenOptional = verificationTokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()){
            throw new BadRequestException("This Token Does Not Exist");
        }
        else {
            VerificationToken tempToken = tokenOptional.get();

            if (LocalDateTime.now().isBefore(tempToken.getExpiredDateTime())){
                User user = tempToken.getUser();
                user.setVerified(true);
                userRepository.save(user);
                tempToken.setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                verificationTokenRepository.save(tempToken);
            }
            else{
                throw new BadRequestException("This token is expired!");
            }
        }
    }

    private VerificationToken generateToken(User user){
        VerificationToken token = new VerificationToken(user);
        verificationTokenRepository.save(token);
        return token;
    }
}
