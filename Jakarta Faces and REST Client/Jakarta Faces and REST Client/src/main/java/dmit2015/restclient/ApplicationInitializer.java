package dmit2015.restclient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class ApplicationInitializer {
    private final Logger _logger = Logger.getLogger(ApplicationInitializer.class.getName());
    @Inject
    @RestClient
    private BeerMpRestClient _beerRestClient;
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        _logger.info("Preloading data");

        try {
            var faker = new Faker();

            for(int count = 1; count <= 10; count++){
                var newBeer = new Beer();

                newBeer.setName(faker.beer().name());
                newBeer.setBrand(faker.beer().brand());
                newBeer.setStyle(faker.beer().style());

                _beerRestClient.create(newBeer);
            }

        } catch (Exception ex) {
            _logger.log(Level.FINE, ex.getMessage(), ex);
        }

    }
}