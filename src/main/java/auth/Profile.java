package auth;

/**
 * Created by Никита on 25.10.2016.
 */

import javax.ws.rs.Path;

import classes.Token;
import classes.TokenStorage;
import classes.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.ConcurrentHashMap;

import static auth.Authentication.*;
import static auth.AuthenticationFilter.*;

@Path("/profile")
public class Profile {
    private static ConcurrentHashMap<String, User> users = getUsers();
    private static TokenStorage tokens = getTokens();
    private static final Logger log = LogManager.getLogger(Profile.class);

    @POST
    @Path("name")
    @Authorized
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response changeName(@FormParam("name") String name,
                               ContainerRequestContext requestContext) {

        // Get the HTTP Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();
        if (token == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            validateToken(token);
        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if (name == null) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        User ex = users.get(tokens.getUserByStringToken(token));
        String oldName =ex.getName();
        ex.setName(name);

        log.info("User '{}' has changed his name to '{}'", oldName, name);
        return Response.ok("User " + oldName + " has changed his name to " + name).build();

    }
}