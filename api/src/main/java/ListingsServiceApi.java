import beans.ListingsBean;
import classes.Listing;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

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

    @Operation(description = "Get all listings.", summary = "Get all booking listings posted.")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of all listings",
                    content = @Content(schema = @Schema(implementation = Listing.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getAllListings() {
        List<Listing> listing = listingsBean.getListings();
        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @Operation(description = "Get a listing by id.", summary = "Get listing by id")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Get listing by id",
                    content = @Content(
                            schema = @Schema(implementation = Listing.class))
            )})
    @GET
    @Path("/{listingId}")
    public Response getListingById(@Parameter(description = "Listing ID") @PathParam("listingId") Integer listingId) {
        Listing listing = listingsBean.getListingById(listingId);
        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @Operation(description = "Add listing.", summary = "Add listing")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Listing successfully added."
            ),
            @APIResponse(responseCode = "400", description = "Validation error. Check request parameters")
    })
    @POST
    public Response createListing(@RequestBody(description="DTO object to represent Listing", required = true, content = @Content(schema = @Schema(implementation = Listing.class)))
                                              Listing listing) {
        if(listing.getTitle() == null || listing.getDescription() == null || listing.getType() == null ||
                listing.getMonthlyPrice() == null || listing.getOwnerId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            listing = listingsBean.createListing(listing);
        }

        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @Operation(description = "Reserve listing.", summary = "Reserve listing. This usually gets called from " +
            "ReservationsService.")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Listing successfully reserved."
            ),
            @APIResponse(responseCode = "400", description = "Validation error. Check request parameters")
    })
    @POST
    @Path("reserveListing/{listingId}")
    public Response reserveListing( @Parameter(description="Listing ID", required = true) @Parameter(description="Reservation ID", required = true)
                                    @PathParam("listingId") Integer listingId, Integer reservationId) {
        Listing listing = listingsBean.reserveListing(listingId, reservationId);

        return Response.status(Response.Status.OK).entity(listing).build();
    }

    @Path("circuitBreaker/{listingId}")
    @GET
    public Response testCircuitBreaker(@PathParam("listingId") Integer listingId) {
        try {
            Listing listing = listingsBean.listingForTolerance(listingId);
            return Response.status(Response.Status.OK).entity(listing).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
