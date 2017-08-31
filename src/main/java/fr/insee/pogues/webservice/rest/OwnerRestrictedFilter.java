package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.user.query.UserServiceQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    static final Logger logger = LogManager.getLogger(OwnerRestrictedFilter.class);

    @Autowired
    private UserServiceQuery userServiceQuery;

    @Autowired
    private QuestionnairesService questionnairesService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        try {
            ContainerRequest request = (ContainerRequest) requestContext;
            request.bufferEntity();
            String id = requestContext.getUriInfo().getPathParameters().getFirst("id");
            logger.debug("XXX id: " + id);
            if (null == id) {
                // need a body to continue
                String message = "No id path param found to perform authorization";
                logger.error(message);
                throw new PoguesException(400, "Bad Request", message);
            }
            JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
            if(questionnaire.isEmpty()){
                // need an object to continue
                throw new PoguesException(404, "Not Found", "Entity not found @id: " + id);
            }
            Object owner = questionnaire.get("owner");
            logger.debug("XXX questionnaire owner: " + owner);
            // Filter request
            Principal principal = requestContext.getSecurityContext()
                    .getUserPrincipal();
            if (null == principal) {
                throw new PoguesException(401, "Unauthenticated", "You are not logged in");
            }
            String permission = userServiceQuery
                    .getNameAndPermissionByID(principal.getName())
                    .getPermission();
            logger.debug("XXX user permission: " + permission);
            if (null == permission || !permission.equals(owner.toString())) {
                throw new PoguesException(403, "Unauthorized", "This object is not yours");
            }
        } catch (PoguesException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new PoguesException(500, "Unexpected error", e.getMessage());
        }

    }
}
