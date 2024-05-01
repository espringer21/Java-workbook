package dmit2015.repository;

import dmit2015.entity.Beer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class BeerInitializer {
    private final Logger _logger = Logger.getLogger(BeerInitializer.class.getName());

    @Inject
    private BeerRepository _beerRepository;


    /**
     * Using the combination of `@Observes` and `@Initialized` annotations, you can
     * intercept and perform additional processing during the phase of beans or events
     * in a CDI container.
     * <p>
     * The @Observers is used to specify this method is in observer for an event
     * The @Initialized is used to specify the method should be invoked when a bean type of `ApplicationScoped` is being
     * initialized
     * <p>
     * Execute code to create the test data for the entity.
     * This is an alternative to using a @WebListener that implements a ServletContext listener.
     * <p>
     * ]    * @param event
     */
    public void initialize(@Observes @Initialized(ApplicationScoped.class) Object event) {
        _logger.info("Initializing Beer");

        if (_beerRepository.count() == 0) {

            // You could hard code the test data
            try {
                // TODO: Create a new entity instance
                // TODO: Set the properties of the entity instance
                // TODO: Add the entity instance to the JPA repository
                Beer beer1 = new Beer();
                beer1.setName("Rocky Mountain Red");
                beer1.setStyle("Alberta Amber Ale");
                beer1.setBrand("Alberta Craft Breweries");
                _beerRepository.add(beer1);

                Beer beer2 = new Beer();
                beer2.setName("Blue Moon Mountain");
                beer2.setStyle("BC Amber Ale");
                beer2.setBrand("BC Craft Breweries");
                _beerRepository.add(beer2);

                Beer beer3 = new Beer();
                beer3.setName("Green Bear Cabin");
                beer3.setStyle("ON Ale");
                beer3.setBrand("ON Breweries");
                _beerRepository.add(beer3);

                Beer beer4 = new Beer();
                beer4.setName("Red River");
                beer4.setStyle("PEI Wiskey");
                beer4.setBrand("PEI Breweries");
                _beerRepository.add(beer4);


            } catch (Exception ex) {
                _logger.fine(ex.getMessage());
            }

            _logger.info("Created " + _beerRepository.count() + " records.");
        }
    }
}