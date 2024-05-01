package dmit2015.faces;

import dmit2015.restclient.Beer;
import dmit2015.restclient.BeerMpRestClient;
import lombok.Getter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            beerMap = _beerMpRestClient.findAll();
        } catch (Exception ex) {
            Messages.addGlobalError(ex.getMessage());
        }
    }
}
