package dmit2015.faces;

import dmit2015.restclient.Beer;
import dmit2015.restclient.BeerMpRestClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.json.JsonObject;
import lombok.Getter;
import net.datafaker.Faker;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.util.Messages;

@Named("currentBeerCreateView")
@RequestScoped
public class BeerCreateView {
    @Inject
    private FirebaseLoginSession _firebaseLoginSession;

    @Inject
    @RestClient
    private BeerMpRestClient _beerMpRestClient;

    @Getter
    private Beer newBeer = new Beer();

    public void randomBeer(){

        var faker = new Faker();
        newBeer.setName(faker.beer().name());
        newBeer.setBrand(faker.beer().brand());
        newBeer.setStyle(faker.beer().style());
    }
    public void clearForm() {

        newBeer.setName(null);
        newBeer.setBrand(null);
        newBeer.setStyle(null);
    }

    public String onCreateNew() {
        String token = _firebaseLoginSession.getFirebaseUser().getIdToken();
        String userUID = _firebaseLoginSession.getFirebaseUser().getLocalId();
        String nextPage = null;;
        try {
            JsonObject responseBody = _beerMpRestClient.create(userUID,newBeer,token);
            String documentKey = responseBody.getString("name");
            newBeer = new Beer();
            Messages.addFlashGlobalInfo("Create was successful. {0}", documentKey);
            nextPage = "index?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }

}