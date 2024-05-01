package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.entity.BeerItem;
import dmit2015.repository.BeerItemRepository;
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
@Path("Beer")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class BeerItemResource {

//    @Context
    @Inject
    private UriInfo uriInfo;

    @Inject
    private BeerItemRepository beerItemRepository;

    @POST   // POST: /restapi/TodoItems
    public Response postBeerItem(BeerItem newBeerItem) {
        if (newBeerItem == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(newBeerItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        beerItemRepository.add(newBeerItem);
        URI beerItemsUri = uriInfo.getAbsolutePathBuilder().path(newBeerItem.getId().toString()).build();
        return Response.created(beerItemsUri).build();
    }

    @GET    // GET: /restapi/TodoItems/5
    @Path("{id}")
    public Response getBeerItem(@PathParam("id") Long id) {
        Optional<BeerItem> optionalBeerItem = beerItemRepository.findById(id);

        if (optionalBeerItem.isEmpty()) {
            throw new NotFoundException();
        }
        BeerItem existingBeerItem = optionalBeerItem.get();

        return Response.ok(existingBeerItem).build();
    }

    @GET    // GET: /restapi/TodoItems
    public Response getBeerItems() {
        return Response.ok(beerItemRepository.findAll()).build();
    }

    @PUT    // PUT: /restapi/TodoItems/5
    @Path("{id}")
    public Response updateBeerItem(@PathParam("id") Long id, BeerItem updatedBeerItem) {
        if (!id.equals(updatedBeerItem.getId())) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(updatedBeerItem);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        BeerItem existingBeerItem = beerItemRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        // Copy data from the updated entity to the existing entity
        existingBeerItem.setVersion(updatedBeerItem.getVersion());
        existingBeerItem.setName(updatedBeerItem.getName());
        existingBeerItem.setStyle(updatedBeerItem.getStyle());
        existingBeerItem.setBrand(updatedBeerItem.getBrand());
        existingBeerItem.setDone(updatedBeerItem.isDone());

        try {
            beerItemRepository.update(existingBeerItem);
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

        return Response.ok(existingBeerItem).build();
    }

    @DELETE // DELETE: /restapi/TodoItems/5
    @Path("{id}")
    public Response deleteBeerItem(@PathParam("id") Long id) {

        BeerItem existingBeerItem = beerItemRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);
        beerItemRepository.deleteById(id);

        return Response.noContent().build();
    }

}