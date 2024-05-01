package dmit2015.faces;


import dmit2015.restclient.BeerItemRbac;
import dmit2015.restclient.BeerItemRbacMpRestClient;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import java.io.Serializable;
import java.util.Optional;


@Named("currentBeerRbacEditController")
@ViewScoped
public class BeerRbacEditController implements Serializable {

    @Inject
    private BeerItemRbacMpRestClient beerRbacMpRestClient;

    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private Long id;

    @Getter
    private BeerItemRbac existingBeerItemRbac;

    @Inject
    private LoginSession loginSession;

    @PostConstruct
    public void init() {
        if (id != null) {
            Optional<BeerItemRbac> optionalBeerItemRbac = Optional.ofNullable(beerRbacMpRestClient.findById(id, loginSession.getAuthorization()));
            optionalBeerItemRbac.ifPresent(BeerItemRbac -> existingBeerItemRbac = BeerItemRbac);
        } else {
            Faces.redirect("index.xhtml");
        }
    }

    public String onUpdate() {
        String nextPage = null;
        try {
            beerRbacMpRestClient.update(id, existingBeerItemRbac, loginSession.getAuthorization());
            Messages.addFlashGlobalInfo("Update was successful.");
            nextPage ="index?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Update was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }
    public String onDelete() {
        String nextPage = null;
        try{
            beerRbacMpRestClient.delete(existingBeerItemRbac.getId(), loginSession.getAuthorization());
            Messages.addFlashGlobalInfo("Delete was successful");
            nextPage = "index?faces-redirect=true";
        }catch(Exception e){
            e.printStackTrace();
            Messages.addGlobalError("Delete was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }
}