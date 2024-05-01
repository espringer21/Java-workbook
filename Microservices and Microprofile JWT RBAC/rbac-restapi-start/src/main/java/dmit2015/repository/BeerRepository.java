package dmit2015.repository;

import common.jpa.AbstractJpaRepository;
import dmit2015.entity.Beer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class BeerRepository extends AbstractJpaRepository<Beer, Long> {
    public BeerRepository() {
        super(Beer.class);
    }

}