package dmit2015.faces;

import dmit2015.restclient.BeerItemMultiUser;
import dmit2015.restclient.BeerItemMultiUserMpRestClient;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("currentBeerMultiUserListView")
@ViewScoped
public class BeerMultiUserListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private LoginSession _loginSession;

    @Inject
    @RestClient
    private BeerItemMultiUserMpRestClient _beerMultiUserMpRestClient;

    @Getter
    private List<BeerItemMultiUser> beerMultiUserList;

//    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            String bearerAuth = _loginSession.getAuthorization();
            beerMultiUserList = _beerMultiUserMpRestClient.findAll(bearerAuth);
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}