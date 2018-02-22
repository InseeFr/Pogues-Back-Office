package fr.insee.pogues.user.service;

import fr.insee.pogues.user.model.User;
import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * User Service to assume the identity service of Pogues UI in JSON
 *
 * @author I6VWID
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserServiceQuery userServiceQuery;

    public JSONObject getUserID(HttpServletRequest request) throws Exception {
        Principal principal = request.getUserPrincipal();
        if (null == principal) {
            throw new PoguesException(403, "Not authenticated", "No user principal found, are you authenticated ?");
        }
        JSONObject json = new JSONObject();
        json.put("id", principal.getName());
        return json;
    }


    public User getNameAndPermission(HttpServletRequest request) throws Exception {
    	String id = null;
    	if(request.getUserPrincipal()!=null){
    		id = request.getUserPrincipal().getName();
    	}
        return userServiceQuery.getNameAndPermissionByID(id);
    }


    public List<String> getPermissions() throws Exception {
        return userServiceQuery.getPermissions();
    }

}
