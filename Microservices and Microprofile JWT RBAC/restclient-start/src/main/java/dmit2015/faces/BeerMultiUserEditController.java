package dmit2015.faces;

import dmit2015.restclient.BeerItemMultiUser;
import dmit2015.restclient.BeerItemMultiUserMpRestClient;
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

@Named("currentBeerMultiUserEditController")
@ViewScoped
public class BeerMultiUserEditController implements Serializable {

    @Inject
    private BeerItemMultiUserMpRestClient beerMultiUserMpRestClient;


    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private Long id;

    @Getter
    @Setter
    private BeerItemMultiUser existingBeerItemMultiUser;

    @Inject
    private LoginSession loginSession;

    @PostConstruct
    public void init() {
        if (id != null) {
            Optional<BeerItemMultiUser> optionalBeerItemMultiUser = Optional.ofNullable(beerMultiUserMpRestClient.findById(id, loginSession.getAuthorization()));
            optionalBeerItemMultiUser.ifPresent(beerItemMultiUser -> existingBeerItemMultiUser = beerItemMultiUser);
        } else {
            Faces.redirect("index.xhtml");
        }
    }

    public String onUpdate() {
        try {
            beerMultiUserMpRestClient.update(id, existingBeerItemMultiUser, loginSession.getAuthorization());
            Messages.addFlashGlobalInfo("Update was successful.");
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Update was not successful. {0}", e.getMessage());
        }
        return "index?faces-redirect=true";
    }
    public String onDelete() {
        String nextPage = null;
        try{
            beerMultiUserMpRestClient.delete(existingBeerItemMultiUser.getId(), loginSession.getAuthorization());
            Messages.addFlashGlobalInfo("Delete was successful");
            nextPage = "index?faces-redirect=true";
        }catch(Exception e){
            e.printStackTrace();
        }
        return nextPage;
    }

}
