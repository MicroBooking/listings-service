package beans;

import classes.Listing;
import converters.ListingConverter;
import entities.ListingEntity;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
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

   public Listing reserveListing(Integer listingId, Integer reservationId) {
            ListingEntity listingEntity = em.find(ListingEntity.class, listingId);
            listingEntity.setReserved(true);
            listingEntity.setReservationId(reservationId);
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

   @CircuitBreaker(requestVolumeThreshold = 1, delay=15000, failureRatio = 0.2)
   @Fallback(fallbackMethod = "listingFallback")
   public Listing listingForTolerance(Integer listingId) throws Exception {
        int i = (int)(Math.random() * 5) % 5;
        if (i == 0 || i == 1) {
            throw new Exception("Errored");
        }
        return getListingById(listingId);
   }

   public Listing listingFallback(Integer listingId) {
        Listing deflisting = new Listing();
        deflisting.setTitle("Default fallback title");
        return deflisting;
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
