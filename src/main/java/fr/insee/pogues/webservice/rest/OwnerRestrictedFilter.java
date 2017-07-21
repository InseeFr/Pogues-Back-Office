package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.user.query.UserServiceQuery;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by acordier on 06/07/17.
 */
@Provider
@OwnerRestricted
public class OwnerRestrictedFilter implements ContainerRequestFilter {

    static final Logger logger = Logger.getLogger(OwnerRestrictedFilter.class);

    @Autowired
    private UserServiceQuery userServiceQuery;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            ContainerRequest request = (ContainerRequest) requestContext;
            request.bufferEntity();
            JSONObject json = request.readEntity(JSONObject.class);
            if(null == json){
                // need a body to work
                logger.warn("Tried to call OwnerRestricted filter on request without a body");
                return;
            }
            String owner = json.get("owner").toString();
            if(null == owner){
                // need a owner attribute to work
                logger.warn("Tried to call OwnerRestricted filter on body without a owner attribute");
                return;
            }
            // Filter request
            Principal principal = requestContext.getSecurityContext()
                    .getUserPrincipal();
            if (null == principal) {
                throw new PoguesException(401, "Unauthenticated", "You are not logged in");
            }
            String permission = userServiceQuery
                    .getNameAndPermissionByID(principal.getName())
                    .getPermission();
            if (null == permission || !permission.equals(owner)) {
                throw new PoguesException(403, "Unauthorized", "This object is not yours");
            }
        } catch(Exception e){
            throw new PoguesException(500, "Unexpected error", e.getMessage());
        }

    }
}