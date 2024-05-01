package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.dto.BeerItemDto;
import dmit2015.dto.BeerItemMapper;
import dmit2015.entity.BeerItem;
import dmit2015.repository.BeerItemRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Web API with CRUD methods for managing TodoItem.
 *
 *  URI						        Http Method     Request Body		                            Description
 * 	----------------------          -----------		-------------------------------------------     ------------------------------------------
 *	/restapi/TodoItemsDto		    POST	      	{                                               Create a new TodoItem
 *                                                  "name":"Demo DMIT2015 assignment 1",
*                                         	        "complete":false
 *                                         	        }
 * 	/restapi/TodoItemsDto/{id}		GET			                                                    Find one TodoItem with a id value
 * 	/restapi/TodoItemsDto		    GET			                                                    Find all TodoItem
 * 	/restapi/TodoItemsDto/{id}      PUT             {
 * 	                                                "id":1,                                         Update the TodoItem
 * 	                                                "name":"Demo DMIT2015 assignment 1",
 *                                                  "complete":true
 *                                                  }
 *
 * /restapi/TodoItemsDto/{id}		DELETE			                                                Remove the TodoItem
 *
 *
 */

@ApplicationScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("Beer")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class BeerItemDtoResource {

    @Inject
    private UriInfo uriInfo;

    @Inject
    private BeerItemRepository beerItemRepository;

    @POST   // POST: restapi/TodoItemsDto
    public Response postBeerItem(@Valid BeerItemDto dto) {
        if (dto == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

//        TodoItem newTodoItem = mapFromDto(dto);
        BeerItem newBeerItem = BeerItemMapper.INSTANCE.toEntity(dto);
        beerItemRepository.add(newBeerItem);

        URI beerItemsUri = uriInfo.getAbsolutePathBuilder().path(newBeerItem.getId().toString()).build();
        return Response.created(beerItemsUri).build();
    }

    @GET    // GET: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response getBeerItem(@PathParam("id") Long id) {
        Optional<BeerItem> optionalBeerItem = beerItemRepository.findById(id);

        if (optionalBeerItem.isEmpty()) {
            throw new NotFoundException();
        }
        BeerItem existingBeerItem = optionalBeerItem.get();
//        TodoItemDto dto = mapToDto(existingTodoItem);
        BeerItemDto dto = BeerItemMapper.INSTANCE.toDto(existingBeerItem);

        return Response.ok(dto).build();
    }

    @GET    // GET: restapi/TodoItemsDto
    public Response getBeerItems() {
        return Response.ok(beerItemRepository.findAll()
                .stream()
//                .map(this::mapToDto)
                .map(BeerItemMapper.INSTANCE::toDto)
                .collect(Collectors.toList()))
                .build();
    }

    @PUT    // PUT: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response updateBeerItem(@PathParam("id") Long id, BeerItemDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Optional<BeerItem> optionalBeerItem = beerItemRepository.findById(id);
        if (optionalBeerItem.isEmpty()) {
            throw new NotFoundException();
        }

        String errorMessage = BeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

        BeerItem existingBeerItem = optionalBeerItem.orElseThrow();
        // Copy data from the updated entity to the existing entity
        existingBeerItem.setVersion(dto.getVersion());
        existingBeerItem.setName(dto.getName());
        existingBeerItem.setStyle(dto.getStyle());
        existingBeerItem.setBrand(dto.getBrand());
        existingBeerItem.setDone(dto.isComplete());

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
//        todoItemRepository.update(id, mapFromDto(dto));

        BeerItemDto existingDto = BeerItemMapper.INSTANCE.toDto(existingBeerItem);
        return Response.ok(existingDto).build();
    }

    @DELETE // DELETE: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response deleteBeerItem(@PathParam("id") Long id) {
        Optional<BeerItem> optionalBeerItem = beerItemRepository.findById(id);

        if (optionalBeerItem.isEmpty()) {
            throw new NotFoundException();
        }

        beerItemRepository.deleteById(id);

        return Response.noContent().build();
    }

//    private TodoItemDto mapToDto(TodoItem todoItem) {
//        return new TodoItemDto(todoItem.getId(), todoItem.getTask(), todoItem.isDone(), todoItem.getVersion());
//    }
//
//    private TodoItem mapFromDto(TodoItemDto dto) {
//        return new TodoItem(dto.getId(), dto.getName(), dto.isComplete());
//    }

}