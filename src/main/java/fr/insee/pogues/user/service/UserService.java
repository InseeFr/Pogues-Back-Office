package fr.insee.pogues.user.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by acordier on 05/07/17.
 */
public interface UserService {
    /**
     * A method to get the user id of the connected user
     *
     * @return the user
     */
    String getUserID(HttpServletRequest request);
    /**
     * A method to get the user attributes of the connected user
     *
     * @return the user
     */
    String getNameAndPermission(HttpServletRequest request);
    /**
     * A method to list all available permissions
     *
     * @return the permissions list
     */
    List<String> getPermissions() throws Exception;
}
