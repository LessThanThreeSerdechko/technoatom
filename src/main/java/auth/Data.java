package auth;

/**
 * Created by Никита on 25.10.2016.
 */

import classes.TokenStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static auth.Authentication.*;


@Path("/data")
public class Data {

    private static final Logger log = LogManager.getLogger(Data.class);
    private static TokenStorage tokens = getTokens();

    @GET
    @Path("users")
    @Produces("application/json")
    public Response getLogins() {

        StringBuffer str = new StringBuffer("{\"users\" : [");
        for (String login : tokens.getTokenMap().keySet()) {
            String ex = login.toString();
            //  String ex = tokens.getTokenMap().get(login).toString();
            String token = tokens.getToken(login).toString();
            str.append("{"+ex+":"+token+"}" + ", ");
        }
        str.deleteCharAt(str.length()-1);
        str.deleteCharAt(str.length()-1);
        str.append(" ] }");
        String kek = new String(str);
        Gson gson = new Gson();
        JsonObject json = new JsonParser().parse(kek).getAsJsonObject();
        return Response.ok(gson.toJson(json)).build();

    }


}
