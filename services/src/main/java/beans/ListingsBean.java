package beans;

import classes.Listing;
import converters.ListingConverter;
import entities.ListingEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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

    public Listing getListingById(Integer id) {
        ListingEntity listingEntity = em.find(ListingEntity.class, id);

        if (listingEntity == null) {
            throw new NotFoundException();
        }

        Listing listing = ListingConverter.toDto(listingEntity);

        return listing;
    }

   public Listing createListing(Listing listing) {
        ListingEntity listingEntity = ListingConverter.toEntity(listing);

       try {
           beginTx();
           em.persist(listingEntity);
           commitTx();
       }
       catch (Exception e) {
           rollbackTx();
       }

       if (listingEntity.getId() == null) {
           throw new RuntimeException("Entity was not persisted");
       }


        return ListingConverter.toDto(listingEntity);
   }

   public Listing reserveListing(Integer listingId) {
            ListingEntity listingEntity = em.find(ListingEntity.class, listingId);
            listingEntity.setReserved(true);
           try {
               beginTx();
               em.merge(listingEntity);
               commitTx();
           }
           catch (Exception e) {
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
