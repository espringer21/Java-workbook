package dmit2015.faces;

import dmit2015.restclient.BeerItemRbac;
import dmit2015.restclient.BeerItemRbacMpRestClient;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.util.Messages;

@Named("currentBeerRbacCreateView")
@RequestScoped
public class BeerRbacCreateView {

    @Inject
    private LoginSession _loginSession;

    @Inject
    @RestClient
    private BeerItemRbacMpRestClient _beerRbacMpRestClient;

    @Getter
    private BeerItemRbac newbeerItemRbac = new BeerItemRbac();

    public String onCreateNew() {
        String nextPage = null;
        try {
            String bearerAuth = _loginSession.getAuthorization();
            Response response = _beerRbacMpRestClient.create(newbeerItemRbac, bearerAuth);
            String location = response.getHeaderString("Location");
            String idValue = location.substring(location.lastIndexOf("/") + 1);
            newbeerItemRbac = new BeerItemRbac();
            Messages.addFlashGlobalInfo("Create was successful. {0}", idValue);
            nextPage = "index?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }

}