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

import java.net.URI;
import java.util.Optional;


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
    private UriInfo uriInfo;

    @Inject
    private BeerRepository beerRepository;

//    @RolesAllowed({"Administration","Executive","Finance"})
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

        return Response.ok(existingBeer).build();
    }

    @GET    // GET: /restapi/TodoItems
    public Response getBeerItems() {
        return Response.ok(beerRepository.findAll()).build();
    }

    @RolesAllowed({"Administration","Executive"})
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

        // Copy data from the updated entity to the existing entity
        existingBeer.setVersion(updatedBeer.getVersion());
        existingBeer.setDone(updatedBeer.isDone());
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

    @RolesAllowed({"Executive"})
    @DELETE // DELETE: /restapi/TodoItems/5
    @Path("{id}")
    public Response deleteBeerItem(@PathParam("id") Long id) {

        Beer existingBeer = beerRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);
        beerRepository.deleteById(id);

        return Response.noContent().build();
    }

}