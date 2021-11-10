package beans;

import classes.Listing;
import converters.ListingConverter;
import entities.ListingEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ListingsBean {
    private Logger log = Logger.getLogger(ListingsBean.class.getName());

    @Inject
    private EntityManager em;


    public List<Listing> getListings() {
        TypedQuery<ListingEntity> query = em.createNamedQuery(
                "ListingEntity.getAll", ListingEntity.class);
        List<ListingEntity> resultList = query.getResultList();

        return resultList.stream().map(ListingConverter::toDto).collect(Collectors.toList());
   }

   public Listing createListing(Listing listing) {
        ListingEntity listingEntity = ListingConverter.toEntity(listing);

        try {
            beginTx();
            em.persist(listingEntity);
            commitTx();
        }
        catch (Exception e){
            rollbackTx();
        }

        return ListingConverter.toDto(listingEntity);
   }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
