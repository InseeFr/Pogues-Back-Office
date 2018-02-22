package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.user.query.UserServiceQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.server.ContainerRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * Created by acordier on 06/07/17.
 */
@Provider
@Component
@OwnerRestricted
public class OwnerRestrictedFilter implements ContainerRequestFilter {

	static final Logger logger = LogManager.getLogger(OwnerRestrictedFilter.class);

	@Value("${fr.insee.pogues.authentication}")
	Boolean authentication;

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
			if (null == id) {
				// need a body to continue
				String message = "No id path param found to perform authorization";
				logger.error(message);
				throw new PoguesException(400, "Bad Request", message);
			}
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			if (null == questionnaire) {
				return;
			}
			Object owner = questionnaire.get("owner");
			if (null == owner) {
				// need a body to continue
				String message = "Could not perform security checks: Entity does not contains a owner attribute";
				logger.error(message);
				throw new PoguesException(500, "Invalid Data", message);
			}
			if (authentication != null) {// Filter request
				if (authentication) {
					Principal principal = requestContext.getSecurityContext().getUserPrincipal();
					if (null == principal) {
						throw new PoguesException(401, "Unauthenticated", "You are not logged in");
					}
					String permission = userServiceQuery.getNameAndPermissionByID(principal.getName()).getPermission();
					if (null == permission || !permission.equals(owner.toString())) {
						throw new PoguesException(403, "Unauthorized", "This object is not yours");
					}
				}
			}else{
				Principal principal = requestContext.getSecurityContext().getUserPrincipal();
				if (null == principal) {
					throw new PoguesException(401, "Unauthenticated", "You are not logged in");
				}
				String permission = userServiceQuery.getNameAndPermissionByID(principal.getName()).getPermission();
				if (null == permission || !permission.equals(owner.toString())) {
					throw new PoguesException(403, "Unauthorized", "This object is not yours");
				}
			}
		} catch (PoguesException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new PoguesException(500, "Unexpected error", e.getMessage());
		}

	}
}
