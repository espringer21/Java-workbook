package dmit2015.faces;

import dmit2015.restclient.FirebaseAuthenticationMpRestClient;
import dmit2015.restclient.FirebaseUser;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.cdi.Param;
import org.omnifaces.util.Messages;

import java.io.Serializable;

@Named
@ViewScoped
public class FirebaseSignup implements Serializable {

    private static final long serialVersionUID = 1L;

    private final FirebaseAuthenticationMpRestClient signupService;
    @Inject
    @RestClient
    private FirebaseAuthenticationMpRestClient _loginService;
    @Inject
    private FirebaseLoginSession _firebaseLoginSession;

    private final String firebaserestApiKey;

    @NotBlank(message = "Email value is required.")
    @Pattern(regexp = ".+@.+\\..+", message = "Invalid email format.")
    @Getter
    @Setter
    private String username;

    @NotBlank(message = "Password value is required.")
    @Size(min = 12, message = "Password must be at least 12 characters long.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit.")
    @Getter
    @Setter
    private String password;
    @Param
    private String requestURI;

    @Inject
    public FirebaseSignup(
            @RestClient FirebaseAuthenticationMpRestClient signupService,
            FirebaseLoginSession firebaseSignupSession,
            @ConfigProperty(name = "firebase.webapikey") String firebaserestApiKey) {
        this.signupService = signupService;
        this.firebaserestApiKey = firebaserestApiKey;
    }

    public String submit() {
        String nextPage = null;

        JsonObject credentials = Json.createObjectBuilder()
                .add("email", username)
                .add("password", password)
                .add("returnSecureToken", true)
                .build();

        try {
            signupService.signUp(firebaserestApiKey, credentials);
            Messages.addGlobalInfo("Signup successful!");

            if (requestURI != null && !requestURI.isBlank()) {
                nextPage = requestURI + "?faces-redirect=true";
            } else {
                FirebaseUser loginFirebaseUser = _loginService.signIn(firebaserestApiKey, credentials);
                _firebaseLoginSession.setFirebaseUser(loginFirebaseUser);
                _firebaseLoginSession.setUsername(username);
                nextPage = "/create/index?faces-redirect=true";
            }
        } catch (FirebaseSignupException e) {
            handleFirebaseSignupException(e);
        } catch (Exception e) {
            handleGenericException(e);
        }

        return nextPage;
    }

    private void handleFirebaseSignupException(FirebaseSignupException e) {

        e.printStackTrace();
        Messages.addGlobalError("Signup failed: " + e.getMessage());
    }

    private void handleGenericException(Exception e) {

        e.printStackTrace();


        Messages.addGlobalError("An error occurred while signing up. Please try again later.");
    }
}

class FirebaseSignupException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FirebaseSignupException(String message) {
        super(message);
    }
}
