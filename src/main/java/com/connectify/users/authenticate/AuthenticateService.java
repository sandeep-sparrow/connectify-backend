package com.connectify.users.authenticate;

import com.connectify.users.UserRepository;
import com.connectify.users.UserType;
import com.connectify.users.Users;
import com.connectify.users.config.JwtService;
import com.connectify.users.token.ConfirmationToken;
import com.connectify.users.token.ConfirmationTokenRepository;
import com.connectify.mail.EmailSender;
import com.connectify.users.token.ValidateToken;
import com.connectify.users.token.ValidateTokenRepository;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class AuthenticateService {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ValidateTokenRepository validateTokenRepository;

    public ResponseEntity<?> signup(RegisterRequest request) {
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null ){
            return ResponseEntity.badRequest().body("Fields not provided");
        }
        if (userRepository.findById(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        if (!isValidEmail(request.getEmail())){
            return ResponseEntity.badRequest().body("invalid email provided!");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("email is taken!");
        }
        if (request.getUsername().length() < 8 || request.getPassword().length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials are not long enough");
        }
        Users user = new Users(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), UserType.USER);
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken= new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(120),
                user
        );
        String link = "http://localhost:8080/confirm?token="+token;
        emailSender.sendEmail(request.getEmail(), buildEmail(request.getUsername(), link));
        confirmationTokenRepository.save(confirmationToken);
        return ResponseEntity.ok().body("successfully created user account! Check email to activate account.");
    }

    public ResponseEntity<?> login(AuthenticationRequest request) {
        System.out.println(request);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        if (!userRepository.getReferenceById(request.getUsername()).getEnabled()){
            return ResponseEntity.badRequest().body("Account has not been activated!");
        }

        Users user = userRepository.findById(request.getUsername()).get();
        String jwtToken = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwtToken, user.getTheme(), user.getProfilePic(), user.getUsername());
        return ResponseEntity.ok(authenticationResponse);
    }

    public boolean isValidEmail(String email){
        if (email == null || email.isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Transactional
    public ResponseEntity<?> confirmToken(String token) {
        if (confirmationTokenRepository.findByToken(token).isEmpty()){
            return ResponseEntity.badRequest().body("token not found");
        }
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).get();

        LocalDateTime expiresAt = confirmationToken.getExpiresAt();
        if (expiresAt.isBefore(LocalDateTime.now())){
            confirmationTokenRepository.deleteByToken(token);
            return ResponseEntity.badRequest().body("token expired");
        }

        Users users = confirmationToken.getUsers();
        users.setEnabled(true);
        userRepository.save(users);
        confirmationTokenRepository.deleteByToken(token);
        return ResponseEntity.ok("Successfully activated account!");
    }



    @Transactional
    public ResponseEntity<?> resendToken(String email){
        if (email == null){
            return ResponseEntity.badRequest().body("email field required");
        }
        if (userRepository.findByEmail(email).isEmpty()){
            return ResponseEntity.badRequest().body("Account with email " + email + " does not exist");
        }
        Users users = userRepository.findByEmail(email).get();
        List<ConfirmationToken> openConfirmationTokens = confirmationTokenRepository.findAllByUsers(users);
        for (ConfirmationToken token : openConfirmationTokens){
            confirmationTokenRepository.deleteByToken(token.getToken());
        }
        if (userRepository.findByEmail(email).get().getEnabled()){
            return ResponseEntity.badRequest().body("Account is already activated!");
        }

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken= new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(120),
                users
        );
        confirmationTokenRepository.save(confirmationToken);
        String link  =  "http://localhost:8080/confirm?token="+token;
        emailSender.sendEmail(email, buildEmail(users.getUsername(), link));
        return ResponseEntity.ok("Activation link resent! Check your email inbox to activate your account!");
    }

    public ResponseEntity<?> sendEmailVerification(String email, HttpServletRequest request) {
        System.out.println(email);
        String username = jwtService.getUsername(request);
        System.out.println(username);
        Gson gson = new Gson();
        Users users = userRepository.findById(username).get();
        Map<String, String> response = new HashMap<>();

        if (userRepository.findByEmail(email).isPresent()){
            response.put("status", "taken");
            return ResponseEntity.badRequest().body(gson.toJson(response));
        }

        String token = UUID.randomUUID().toString();

        response.put("status", "valid");

        ValidateToken validateToken = new ValidateToken(token, users, email);

        validateTokenRepository.save(validateToken);

        String link = "http://localhost:8080/validate?token="+token;

        emailSender.sendEmail(email, buildVerifyEmail(username, link));

        return ResponseEntity.ok(gson.toJson(response));
    }

    @Transactional
    public ResponseEntity<?> validateEmailToken(String token){
        if (validateTokenRepository.findByToken(token).isEmpty()){
            return ResponseEntity.badRequest().body("token not found");
        }

        ValidateToken validateToken = validateTokenRepository.findByToken(token).get();

        Date expiresAt = validateToken.getExpiresAt();
        if (expiresAt.getTime() < (new Date().getTime())){
            validateTokenRepository.deleteByToken(token);
            return ResponseEntity.badRequest().body("token expired");
        }

        Users users = validateToken.getUsers();
        users.setEmail(validateToken.getEmail());
        userRepository.save(users);
        validateTokenRepository.deleteByToken(token);

        return ResponseEntity.ok("successfully validated new email!");
    }

    @Transactional
    public void deleteUnauthenticatedAccounts() {
        List<ConfirmationToken> tokens = confirmationTokenRepository.findAllByExpiresAtBefore(LocalDateTime.now());
        for (ConfirmationToken token : tokens){
            Optional<Users> user = userRepository.findById(token.getUsers().getUsername());
            confirmationTokenRepository.deleteByToken(token.getToken());
            user.ifPresent(userRepository::delete);
        }
    }

    @Transactional
    public void invalidateEmailReverificationTokens() {
        List<ValidateToken> tokens = validateTokenRepository.findAllByExpiresAtBefore(new Date());
        for (ValidateToken token : tokens){
            validateTokenRepository.deleteById(token.getId());
        }
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String buildVerifyEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
