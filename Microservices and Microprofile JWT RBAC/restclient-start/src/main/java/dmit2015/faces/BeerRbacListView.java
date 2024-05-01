package dmit2015.faces;

import dmit2015.restclient.BeerItemRbac;
import dmit2015.restclient.BeerItemRbacMpRestClient;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Named("currentBeerRbacListView")
@ViewScoped
public class BeerRbacListView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    @RestClient
    private BeerItemRbacMpRestClient _beerRbacMpRestClient;

    @Getter
    private List<BeerItemRbac> BeerRbacList;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            BeerRbacList = _beerRbacMpRestClient.findAll();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}