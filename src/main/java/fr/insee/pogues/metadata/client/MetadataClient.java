package fr.insee.pogues.metadata.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public interface MetadataClient {

    /**
     * Get an item from its ID
     * @param id - Item identifier (Colectica Identifier attribute)
     * @return
     * @throws Exception
     */
    JSONObject getItem(String id) throws Exception;

    /**
     * Get all nested referenced items for a given ID
     * @param id
     * @return
     * @throws Exception
     */
    JSONArray getChildren(String id) throws Exception;

    /**
     * Get all nested referenced items for a given ID
     * itemQuery JSON reprezentation :
     * <pre>
     *     <code>
     * {
     *  "Identifiers": [
     *          {
     *              "AgencyId": "string",
     *              "Identifier": "string",
     *              "Version": 0
     *          }
     *      ]
     *  }
     *     </code>
     * </pre>
     * @param itemsQuery
     * @return
     * @throws Exception
     */
    JSONArray getItems(JSONObject itemsQuery) throws Exception;
}
