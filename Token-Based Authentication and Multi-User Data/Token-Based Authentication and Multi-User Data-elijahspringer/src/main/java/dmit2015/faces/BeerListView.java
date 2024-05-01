package dmit2015.faces;

import dmit2015.restclient.Beer;
import dmit2015.restclient.BeerMpRestClient;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;


@Named("currentBeerListView")
@ViewScoped
public class BeerListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    @RestClient
    private BeerMpRestClient _beerMpRestClient;

    @Getter
    private Map<String, Beer> beerMap;

    @Inject
    private FirebaseLoginSession _firebaseLoginSession;

    @PostConstruct  // After @Inject is complete
    public void init() {
        String token = _firebaseLoginSession.getFirebaseUser().getIdToken();
        String userID = _firebaseLoginSession.getFirebaseUser().getLocalId();
        try {
            beerMap = _beerMpRestClient.findAll(userID,token);
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}
