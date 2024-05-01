package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.entity.Beer;
import dmit2015.repository.BeerRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.Set;


/**
 * * Web API with CRUD methods for managing TodoItem.
 *
 *  URI						    Http Method     Request Body		                        Description
 * 	----------------------      -----------		------------------------------------------- ------------------------------------------
 *	/restapi/TodoItems			POST			{                                           Create a new TodoItem
 *                                              "name":"Demo DMIT2015 assignment 1",
 *                                         	    "complete":false
 *                                         	    }
 * 	/restapi/TodoItems/{id}		GET			                                                Find one TodoItem with a id value
 * 	/restapi/TodoItems		    GET			                                                Find all TodoItem
 * 	/restapi/TodoItems/{id}     PUT             {                                           Update the TodoItem
 * 	                                            "id":5,
 * 	                                            "name":"Submitted DMIT2015 assignment 7",
 *                                              "complete":true
 *                                              }
 * 	/restapi/TodoItems/{id}		DELETE			                                            Remove the TodoItem
 *
 */

@ApplicationScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("BeerItems")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class BeerResource {

    @Inject
    @Claim(standard = Claims.sub)   // The unique identifier for the user.
    private ClaimValue<String> subject;

    @Inject
    @Claim(standard = Claims.upn)   // The username for the user.
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    @Claim(standard = Claims.groups)    // The roles that the subject is a member of.
    private ClaimValue<Set<String>> optionalGroups;

    @Inject
    private UriInfo uriInfo;

    @Inject
    private BeerRepository beerRepository;

    @RolesAllowed("**")
    @POST   // POST: /restapi/TodoItems
    public Response postBeerItem(Beer newBeer) {
        if (newBeer == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(newBeer);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        String username = optionalUsername.getValue().orElseThrow();
        newBeer.setUsername(username);
        
        beerRepository.add(newBeer);
        URI beerItemsUri = uriInfo.getAbsolutePathBuilder().path(newBeer.getId().toString()).build();
        return Response.created(beerItemsUri).build();
    }

    @RolesAllowed("**")
    @GET    // GET: /restapi/TodoItems/5
    @Path("{id}")
    public Response getBeerItem(@PathParam("id") Long id) {
        Optional<Beer> optionalBeerItem = beerRepository.findById(id);

        if (optionalBeerItem.isEmpty()) {
            throw new NotFoundException();
        }
        Beer existingBeer = optionalBeerItem.get();

        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to get data owned by another user.";
            throw new NotAuthorizedException(message);
        }

        return Response.ok(existingBeer).build();
    }

    @Path("all")
    @GET    // GET: /restapi/TodoItems
    public Response getAllBeerItems() {
        return Response.ok(beerRepository.findAll()).build();
    }

    @RolesAllowed("**")
    @GET    // GET: /restapi/TodoItems
    public Response getBeerItems() {
        String username = optionalUsername.getValue().orElseThrow();
        return Response.ok(beerRepository.findAllByUsername(username)).build();
    }
    @RolesAllowed("**")
    @PUT    // PUT: /restapi/TodoItems/5
    @Path("{id}")
    public Response updateBeerItem(@PathParam("id") Long id, Beer updatedBeer) {
        if (!id.equals(updatedBeer.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(updatedBeer);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        Beer existingBeer = beerRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to update data owned by another user.";
            throw new NotAuthorizedException(message);
        }

        // Copy data from the updated entity to the existing entity
        existingBeer.setVersion(updatedBeer.getVersion());
        existingBeer.setName(updatedBeer.getName());
        existingBeer.setStyle(updatedBeer.getStyle());
        existingBeer.setBrand(updatedBeer.getBrand());

        try {
            beerRepository.update(existingBeer);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("You are updating an old version of the data. Please fetch new version.")
                    .build();
        } catch (Exception ex) {
            return Response
                    .serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        return Response.ok(existingBeer).build();
    }

    @RolesAllowed("**")
    @DELETE // DELETE: /restapi/TodoItems/5
    @Path("{id}")
    public Response deleteBeerItem(@PathParam("id") Long id) {

        Beer existingBeer = beerRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to get delete owned by another user.";
            throw new NotAuthorizedException(message);
        }

        beerRepository.deleteById(id);

        return Response.noContent().build();
    }

}