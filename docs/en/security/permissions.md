# Autorization

Following rules apply for access control

 - Access control applies only on updates (using PUT verb against /persistence/questionnaires/{id})
 - Permissions are verified using the 'permission' attribute of the user under control.
 
User attributes are given by the constructor of the User model entity:

[import:'marker0'](../../../src/main/java/fr/insee/pogues/user/model/User.java)
 
Access control is implemented using a jersey filter defined in  ```OwnerRestrictedFilter.java```. 
This allow us to apply access control by adding the ```@OwnerRestricted``` annotation on any resource accepting a pogues model entity as a payload.

For better comprehension here is the test class fr OwnerRestrictedFilter 

[include](../../../src/test/java/fr/insee/pogues/jersey/TestOwnerRestrictedFilter.java)
