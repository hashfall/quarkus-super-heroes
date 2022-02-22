package io.quarkus.workshop.superheroes.villain;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

import org.jboss.logging.Logger;

import org.jboss.resteasy.reactive.RestPath;

import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.*;

@Path("api/villains")
@Tag(name="villains")
public class VillainResource {

    Logger logger;
    VillainService service;

    public VillainResource(Logger logger, VillainService service) {
        this.logger = logger;
        this.service = service;
    }

    @Operation(summary = "Returns a random villain")
    @GET
    @Path("/random")
    @APIResponse(
         responseCode = "200",
         content = @Content(mediaType = APPLICATION_JSON,
         schema = @Schema(implementation = Villain.class,
         required = true))
    )
    public Response getRandomVillain() {
        Villain villain = service.findRandomVillain();
        logger.debug("Found random villain " + villain);
        return Response.ok(villain).build();
    }

    @Operation(summary = "Returns all the villains from the database")
    @GET
    @APIResponse(
        responseCode = "200",
        content = @Content(mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = Villain.class, type = SchemaType.ARRAY))
    )
    @APIResponse(
        responseCode = "204",
        description = "No Villains"
    )
    public Response getAllVillains() {
        List<Villain> villains = service.findAllVillains();
        logger.debug("Total number of villains " + villains);
        return Response.ok(villains).build();
    }

    @Operation(summary = "Return a villain for a given identifier")
    @GET
    @Path("/{id}")
    @APIResponse(
        responseCode = "200",
        content = @Content(mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = Villain.class))
    )
    @APIResponse(
        responseCode = "204",
        description = "No Villain found for the given identifier"
    )
    public Response getVillain(@RestPath Long id) {
        Villain villain = service.findVillainById(id);
        if(villain != null) {
            logger.debug("Found villain " + villain);
            return Response.ok(villain).build();
        } else {
            logger.debug("No villain found with id " + id);
            return Response.noContent().build();
        }
    }

    @Operation(summary = "Creates a valid villain")
    @POST
    @APIResponse(
        responseCode = "201",
        description = "The URI of the created villain",
        content = @Content(mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = URI.class))
    )
    public Response createVillain(@Valid Villain villain, @Context UriInfo uriInfo) {
        villain = service.persistVillain(villain);
        UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(villain.id));
        logger.debug("New villain created with URI " + builder.build().toString());
        return Response.created(builder.build()).build();
    }

    @Operation(summary = "Updates an existing villain")
    @PUT
    @APIResponse(
        responseCode = "200",
        description = "The updated villain",
        content = @Content(mediaType = APPLICATION_JSON,
        schema = @Schema(implementation = Villain.class))
    )
    public Response updateVillain(@Valid Villain villain) {
        villain = service.updateVillain(villain);
        logger.debug("Villain updated with new value " + villain);
        return Response.ok(villain).build();
    }
    
    @Operation(summary = "Deletes an existing villain")
    @DELETE
    @Path("/{id}")
    @APIResponse(
        responseCode = "204"
    )
    public Response deleteVillain(@RestPath Long id) {
        service.deleteVillain(id);
        logger.debug("Villain deleted with id " + id);
        return Response.noContent().build();
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    @Tag(name="hello")
    public String hello() {
        return "Hello Villain Resource!";
    }

}