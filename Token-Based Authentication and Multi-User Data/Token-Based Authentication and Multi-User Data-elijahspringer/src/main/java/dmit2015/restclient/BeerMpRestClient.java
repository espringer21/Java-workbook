package dmit2015.restclient;

import jakarta.enterprise.context.Dependent;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.LinkedHashMap;

/**
 * The baseUri for the web MpRestClient be set in either microprofile-config.properties (recommended)
 * or in this file using @RegisterRestClient(baseUri = "http://server/path").
 * <p>
 * To set the baseUri in microprofile-config.properties:
 * 1) Open src/main/resources/META-INF/microprofile-config.properties
 * 2) Add a key/value pair in the following format:
 * package-name.ClassName/mp-rest/url=baseUri
 * For example:
 * package-name:    dmit2015.restclient
 * ClassName:       BeerMpRestClient
 * baseUri:         http://localhost:8080/contextName
 * The key/value pair you need to add is:
 * <code>
 * dmit2015.restclient.BeerMpRestClient/mp-rest/url=http://localhost:8080/contextName
 * </code>
 * <p>
 * To use the client interface from an environment does support CDI, add @Inject and @RestClient before the field declaration such as:
 * <code>
 *
 * @Inject
 * @RestClient private BeerMpRestClient _beerMpRestClient;
 * </code>
 * <p>
 * To use the client interface from an environment that does not support CDI, you can use the RestClientBuilder class to programmatically build an instance as follows:
 * <code>
 * URI apiURI = new URI("http://sever/contextName");
 * BeerMpRestClient _beerMpRestClient = RestClientBuilder.newBuilder().baseUri(apiURi).build(BeerMpRestClient.class);
 * </code>
 */
@Dependent
@RegisterRestClient(baseUri = "https://dmit2015-assingment1-espringer-default-rtdb.firebaseio.com")
public interface BeerMpRestClient {

    String DOCUMENT_URL = "/beer/{uid}";

    @POST
    @Path(DOCUMENT_URL + ".json")
    JsonObject create(@PathParam("uid") String userId, Beer newBeer,@QueryParam("auth") String token);

    @GET
    @Path(DOCUMENT_URL + ".json")
    LinkedHashMap<String, Beer> findAll(@PathParam("uid") String userId, @QueryParam("auth") String token);

    @GET
    @Path(DOCUMENT_URL + "/{key}.json")
    Beer findById(@PathParam("uid") String userid, @PathParam("key") String key, @QueryParam("auth") String token);

    @PUT
    @Path(DOCUMENT_URL + "/{key}.json")
    Beer update(@PathParam("uid") String userId, @PathParam("key") String key, Beer updatedBeer, @QueryParam("auth") String token);

    @DELETE
    @Path(DOCUMENT_URL + "/{key}.json")
    void delete(@PathParam("uid") String userId,@PathParam("key") String key, @QueryParam("auth") String token);

}


