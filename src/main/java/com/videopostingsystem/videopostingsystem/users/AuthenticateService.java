package com.videopostingsystem.videopostingsystem.users;

import com.videopostingsystem.videopostingsystem.mail.EmailSender;
import com.videopostingsystem.videopostingsystem.posts.Post;
import com.videopostingsystem.videopostingsystem.posts.PostRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractionRepository;
import com.videopostingsystem.videopostingsystem.posts.interaction.PostInteractions;
import com.videopostingsystem.videopostingsystem.users.token.ConfirmationToken;
import com.videopostingsystem.videopostingsystem.users.token.ConfirmationTokenRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AuthenticateService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostInteractionRepository postInteractionRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailSender emailSender;

    public AuthenticateService(UserRepository userRepository,
                               PostRepository postRepository,
                               PostInteractionRepository postInteractionRepository,
                               ConfirmationTokenRepository confirmationTokenRepository,
                               EmailSender emailSender) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postInteractionRepository = postInteractionRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSender = emailSender;
    }

    public ResponseEntity<?> signup(AuthenticateModel signUp, HttpSession session){
        session.setAttribute("loggedInUser", null);
        if (signUp != null) {
            if (signUp.username() != null && signUp.password() != null) {
                if (userRepository.findById(signUp.username()).isEmpty()) {
                    if (!isValidEmail(signUp.email())){
                        return ResponseEntity.badRequest().body("invalid email provided!");
                    }
                    if (userRepository.findByEmail(signUp.email()).isPresent()){
                        return ResponseEntity.badRequest().body("email is taken!");
                    }
                    if (signUp.username().length() < 8 || signUp.password().length() < 8) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Credentials are not long enough");
                    }
                    Users user;
                    String type = "user";

                    if (signUp.security_clearance() != null) {
                        if (signUp.security_clearance().equals(CONSTANTS.security_clearance)) {
                            user = new Users(signUp.username(), signUp.email(), signUp.password(), "ADMIN");
                            user.setTopCategory("blank");
                            type = "admin";
                        } else {
                            user = new Users(signUp.username(), signUp.email(), signUp.password(), "USER");
                            user.setTopCategory("blank");
                        }
                    } else {
                        user = new Users(signUp.username(), signUp.email(), signUp.password(), "USER");
                        user.setTopCategory("blank");
                    }
                    userRepository.save(user); // save user to the database
                    String token = UUID.randomUUID().toString();
                    ConfirmationToken confirmationToken= new ConfirmationToken(
                            token,
                            LocalDateTime.now(),
                            LocalDateTime.now().plusMinutes(15),
                            user
                    );
                    String link  =  "http://localhost:8080/confirm?token="+token;
                    emailSender.sendEmail(signUp.email(), buildEmail(signUp.username(), link));
                    confirmationTokenRepository.save(confirmationToken);
                    return ResponseEntity.ok().body("successfully created " + type + " account! Check email to activate account.");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Must provide username and password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Must provide username and password");
        }
    }

    public ResponseEntity<?> login(AuthenticateModel login, HttpSession session){
        if (userRepository.findById(login.username()).isPresent()) {
            Users user = userRepository.getReferenceById(login.username());
            if (login.password().equals(user.getPassword())) {
                if (!userRepository.getReferenceById(login.username()).getEnabled()){
                    return ResponseEntity.badRequest().body("Account has not been activated!");
                }
                session.setAttribute("loggedInUser", login.username());
                return ResponseEntity.ok("Login successful");

            } else {
                return ResponseEntity.badRequest().body("Incorrect password");
            }
        } else {
            return ResponseEntity.badRequest().body("Incorrect username");
        }
    }

    @Transactional
    public ResponseEntity<?> deleteAccount(HttpSession session){
        String user = (String) session.getAttribute("loggedInUser");
        if (session.getAttribute("loggedInUser") != null && userRepository.findById(user).isPresent()){
            List<PostInteractions> postInteractions = postInteractionRepository.findByUsers(user);
            for (PostInteractions currPostInteraction : postInteractions){
                postInteractionRepository.deleteById(currPostInteraction.getPostID()+"_"+currPostInteraction.getUsers());
            }
            List<Post> posts = postRepository.findByUsers(user);
            for (Post currPost : posts){
                postRepository.deleteById(currPost.getId());
            }
            List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAllByUsers(userRepository.findById(user).get());
            for (ConfirmationToken token : confirmationTokens){
                confirmationTokenRepository.deleteByToken(token.getToken());
            }
            userRepository.deleteById((String) session.getAttribute("loggedInUser"));
            return ResponseEntity.ok("Successfully deleted account. We're sad to see you go, " + user + "!");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
    }

    @Transactional
    public ResponseEntity<?> deleteAccountAdmin(String user, HttpSession session) {
        String adminAccount = (String) session.getAttribute("loggedInUser");
        if (adminAccount != null && userRepository.findById(adminAccount).isPresent()) {
            if (userRepository.findById(user).isEmpty()) {
                return ResponseEntity.badRequest().body("user does not exist");
            }
            if (userRepository.findById(adminAccount).get().getType().equals("ADMIN")) {
                List<PostInteractions> postInteractions = postInteractionRepository.findByUsers(user);
                for (PostInteractions currPostInteraction : postInteractions) {
                    postInteractionRepository.deleteById(currPostInteraction.getPostID() + "_" + currPostInteraction.getUsers());
                }
                List<Post> posts = postRepository.findByUsers(user);
                for (Post currPost : posts) {
                    postRepository.deleteById(currPost.getId());
                }
                if (!confirmationTokenRepository.findAllByUsers(userRepository.findById(user).get()).isEmpty()) {
                    List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findAllByUsers(userRepository.findById(user).get());
                    for (ConfirmationToken token : confirmationTokens) {
                        confirmationTokenRepository.deleteByToken(token.getToken());
                    }
                    userRepository.deleteById(user);
                    return ResponseEntity.ok("Successfully deleted user " + user);
                } else
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You are not authorized to delete this users account!");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
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
    public ResponseEntity<?> resendToken(@RequestBody String email){
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
                LocalDateTime.now().plusMinutes(15),
                users
        );
        confirmationTokenRepository.save(confirmationToken);
        String link  =  "http://localhost:8080/confirm?token="+token;
        emailSender.sendEmail(email, buildEmail(users.getUsername(), link));
        return ResponseEntity.ok("Activation link resent! Check your email inbox to activate your account!");
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
}
