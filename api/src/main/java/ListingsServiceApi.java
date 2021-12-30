import beans.ListingsBean;
import classes.Listing;
import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("listings")
public class ListingsServiceApi {
    private Logger log = Logger.getLogger(ListingsServiceApi.class.getName());

    @Inject
    private ListingsBean listingsBean;


    @GET
    public Response getAllListings() {
        List<Listing> listing = listingsBean.getListings();
        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @GET
    @Path("/{listingId}")
    public Response getListingById(@PathParam("listingId") Integer listingId) {
        Listing listing = listingsBean.getListingById(listingId);
        return Response.status(Response.Status.OK).entity(listing).build();
    }


    @POST
    public Response createListing(Listing listing) {
        if(listing.getTitle() == null || listing.getDescription() == null || listing.getType() == null ||
                listing.getMonthlyPrice() == null || listing.getOwnerId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            listing = listingsBean.createListing(listing);
        }

        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @POST
    @Path("reserveListing/{listingId}")
    public Response reserveListing(@PathParam("listingId") Integer listingId, Integer reservationId) {
        Listing listing = listingsBean.reserveListing(listingId, reservationId);

        log.info("Got new reservation!");
        return Response.status(Response.Status.OK).entity(listing).build();
    }
}
