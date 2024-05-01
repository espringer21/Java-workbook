package dmit2015.repository;

import dmit2015.entity.Beer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Transactional
public class BeerRepository extends AbstractJpaRepository<Beer, Long> {
    public BeerRepository() {
        super(Beer.class);
    }

    public List<Beer> findAllByUsername(String username) {
        return getEntityManager().createQuery("select o from Beer o where o.username = :usernameValue", Beer.class)
                .setParameter("usernameValue", username)
                .getResultList();
    }
}