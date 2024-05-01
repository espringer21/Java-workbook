package dmit2015.repository;

import dmit2015.entity.BeerItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class BeerItemRepository extends AbstractJpaRepository<BeerItem, Long> {
    public BeerItemRepository() {
        super(BeerItem.class);
    }

}