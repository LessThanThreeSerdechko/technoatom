package auth;

/**
 * Created by Никита on 20.10.2016.
 */

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
import java.util.concurrent.ThreadLocalRandom;

@Path("/auth")
public class Authentication {
    private static final Logger log = LogManager.getLogger(Authentication.class);
    private static ConcurrentHashMap<String, User> users;
    private static TokenStorage tokens;

    // curl -i
    //      -X POST
    //      -H "Content-Type: application/x-www-form-urlencoded"
    //      -H "Host: {IP}:8080"
    //      -d "login={}&password={}"
    // "{IP}:8080/auth/register"
    @POST
    @Path("register")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response register(@FormParam("user") String user,
                             @FormParam("password") String password) {

        if (user == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (users.putIfAbsent(user, new User(user, password)) != null) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }

        log.info("New user '{}' registered", user);
        return Response.ok("User " + user + " registered.").build();
    }

    static {
        users = new ConcurrentHashMap<>();
        users.put("admin", new User("admin", "admin"));
        tokens = new TokenStorage();
        tokens.addToken("admin", new Token(1L));


    }

    // curl -X POST
    //      -H "Content-Type: application/x-www-form-urlencoded"
    //      -H "Host: localhost:8080"
    //      -d "login=admin&password=admin"
    // "http://localhost:8080/auth/login"
    @POST
    @Path("login")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    public Response authenticateUser(@FormParam("user") String user,
                                     @FormParam("password") String password) {

        if (user == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            // Authenticate the user using the credentials provided
            if (!authenticate(user, password)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            // Issue a token for the user
            Long token = issueToken(user);
            log.info("User '{}' logged in", user);

            // Return the token on the response
            return Response.ok(Long.toString(token)).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }


    @POST
    @Path("logout")
    @Authorized
    @Produces("text/plain")
    public Response logout(ContainerRequestContext requestContext) {

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

        String user = tokens.getUserByStringToken(token);
        tokens.removeToken(user);
        log.info("User '{}' logged out", user);
        return Response.ok("User " + user + " logged out.").build();

    }

    private boolean authenticate(String user, String password) throws Exception {
        return password.equals(users.get(user).getPassword());
    }

    private Long issueToken(String user) {
        Long token = null;
        if (tokens.getToken(user)!=null)
            token = tokens.getToken(user).getToken();
        System.out.println(token);
        if (token != null) {
            return token;
        }
        token = ThreadLocalRandom.current().nextLong();
        Token test = new Token(token);
        tokens.addToken(user, test);  // почему всем ключам во всех коллекциях TokenStorage добавляет один и тот же токен, спасите

        return token;
    }

    static void validateToken(String token) throws Exception {
         if (!tokens.containsToken(token)) {
            throw new Exception("Token validation exception");
        }
        log.info("Correct token from '{}'", tokens.getUser(token));
    }

    public static TokenStorage getTokens() {
        return tokens;
    }

    public static ConcurrentHashMap<String, User> getUsers() {
        return users;
    }
}
