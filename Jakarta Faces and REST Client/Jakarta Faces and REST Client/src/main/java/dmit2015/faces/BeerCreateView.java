package dmit2015.faces;

import dmit2015.restclient.Beer;
import dmit2015.restclient.BeerMpRestClient;

import jakarta.json.JsonObject;
import lombok.Getter;
import net.datafaker.Faker;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.util.Messages;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named("currentBeerCreateView")
@RequestScoped
public class BeerCreateView {

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
        String nextPage = null;
        try {
            JsonObject responseBody = _beerMpRestClient.create(newBeer);
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