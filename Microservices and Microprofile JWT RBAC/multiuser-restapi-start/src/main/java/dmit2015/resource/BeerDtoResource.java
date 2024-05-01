package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.dto.BeerDto;
import dmit2015.dto.BeerMapper;
import dmit2015.entity.Beer;
import dmit2015.repository.BeerRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.Valid;
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

@RequestScoped
// This is a CDI-managed bean that is created only once during the life cycle of the application
@Path("BeerDto")	        // All methods of this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)	// All methods this class accept only JSON format data
@Produces(MediaType.APPLICATION_JSON)	// All methods returns data that has been converted to JSON format
public class BeerDtoResource {

    @Inject
    @Claim(standard = Claims.upn)   // The username for the user.
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    @Claim(standard = Claims.groups)    // The roles that the subject is a member of.
    private ClaimValue<Set<String>> optionalGroups;
    @GET
    @Path("token")
    @RolesAllowed("**")
    public String tokenCheck() {
        if (optionalUsername.getValue().isEmpty()) {
            return "No JWT in Http Request";
        }
        String username = optionalUsername.getValue().orElseThrow();
        Set<String> groups  = optionalGroups.getValue();
        return "JWT contains upn "  + username + ", roles: " + groups.toString();
    }



    @Inject
    private UriInfo uriInfo;

    @Inject
    private BeerRepository beerRepository;

    @RolesAllowed("**")
    @POST   // POST: restapi/TodoItemsDto
    public Response postBeerItem(@Valid BeerDto dto) {
        if (dto == null) {
            throw new BadRequestException();
        }

        String errorMessage = BeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

//        TodoItem newTodoItem = mapFromDto(dto);
        Beer newBeer = BeerMapper.INSTANCE.toEntity(dto);
        String username = optionalUsername.getValue().orElseThrow();
        newBeer.setUsername(username);
        beerRepository.add(newBeer);

        URI beerItemsUri = uriInfo.getAbsolutePathBuilder().path(newBeer.getId().toString()).build();
        return Response.created(beerItemsUri).build();
    }

    @RolesAllowed("**")
    @GET    // GET: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response getTodoItem(@PathParam("id") Long id) {
        Optional<Beer> optionalTodoItem = beerRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }
        Beer existingBeer = optionalTodoItem.get();
//        TodoItemDto dto = mapToDto(existingTodoItem);
        BeerDto dto = BeerMapper.INSTANCE.toDto(existingBeer);

        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to get data owned by another user.";
            throw new NotAuthorizedException(message);
        }

        return Response.ok(dto).build();
    }

    @RolesAllowed("**")
    @GET    // GET: restapi/TodoItemsDto
    public Response getTodoItems() {
        String username = optionalUsername.getValue().orElseThrow();
        return Response.ok(beerRepository.findAllByUsername(username)
                .stream()
//                .map(this::mapToDto)
                .map(BeerMapper.INSTANCE::toDto)
                .collect(Collectors.toList()))
                .build();
    }

    @Path("all")
    @GET    // GET: restapi/TodoItemsDto
    public Response getAllTodoItems() {
        return Response.ok(beerRepository.findAll()
                        .stream()
//                .map(this::mapToDto)
                        .map(BeerMapper.INSTANCE::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @RolesAllowed("**")
    @PUT    // PUT: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response updateTodoItem(@PathParam("id") Long id, BeerDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Optional<Beer> optionalTodoItem = beerRepository.findById(id);
        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        String errorMessage = BeanValidator.validateBean(dto);
        if (errorMessage != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }

        Beer existingBeer = optionalTodoItem.orElseThrow();
        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to update data owned by another user.";
            throw new NotAuthorizedException(message);
        }

        // Copy data from the updated entity to the existing entity
        existingBeer.setVersion(dto.getVersion());
        existingBeer.setName(dto.getName());
        existingBeer.setStyle(dto.getStyle());
        existingBeer.setBrand(dto.getBrand());

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
//        todoItemRepository.update(id, mapFromDto(dto));
        BeerDto existingDto = BeerMapper.INSTANCE.toDto(existingBeer);
        return Response.ok(existingDto).build();
    }

    @RolesAllowed("**")
    @DELETE // DELETE: restapi/TodoItemsDto/5
    @Path("{id}")
    public Response deleteTodoItem(@PathParam("id") Long id) {
        Optional<Beer> optionalTodoItem = beerRepository.findById(id);

        if (optionalTodoItem.isEmpty()) {
            throw new NotFoundException();
        }

        Beer existingBeer = optionalTodoItem.orElseThrow();
        String username = optionalUsername.getValue().orElseThrow();
        if (!existingBeer.getUsername().equalsIgnoreCase(username)) {
            final String message = "Access denied. You do not have permission to delete data owned by another user.";
            throw new NotAuthorizedException(message);
        }

        beerRepository.deleteById(id);

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