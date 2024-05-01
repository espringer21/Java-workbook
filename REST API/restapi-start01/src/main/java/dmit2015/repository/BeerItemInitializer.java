package dmit2015.repository;

import dmit2015.entity.BeerItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class BeerItemInitializer {
    private final Logger _logger = Logger.getLogger(BeerItemInitializer.class.getName());

    @Inject
    private BeerItemRepository _beerItemRepository;


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
        _logger.info("Initializing BeerItems");

        if (_beerItemRepository.count() == 0) {

            // You could hard code the test data
            try {
                // TODO: Create a new entity instance
                // TODO: Set the properties of the entity instance
                // TODO: Add the entity instance to the JPA repository
                BeerItem beer1 = new BeerItem();
                beer1.setName("Rocky Mountain Red");
                beer1.setStyle("Alberta Amber Ale");
                beer1.setBrand("Alberta Craft Breweries");
                beer1.setDone(true);
                _beerItemRepository.add(beer1);

                BeerItem beer2 = new BeerItem();
                beer2.setName("Rocky Mountain Blue");
                beer2.setStyle("Alberta Amber Ale");
                beer2.setBrand("Alberta Craft Breweries");
                beer2.setDone(false);
                _beerItemRepository.add(beer2);

                BeerItem beer3 = new BeerItem();
                beer3.setName("Rocky Mountain Green");
                beer3.setStyle("Alberta Amber Ale");
                beer3.setBrand("Alberta Craft Breweries");
                beer3.setDone(false);
                _beerItemRepository.add(beer3);


            } catch (Exception ex) {
                _logger.fine(ex.getMessage());
            }

            _logger.info("Created " + _beerItemRepository.count() + " records.");
        }
    }
}