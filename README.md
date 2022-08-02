# nagarro-assignment

This assignment includes 2 projects as below:
1. Server for the user interface - UserAccount
2. Server for the Database - AccountStatement

Both the projects runs independently. 

UserAccount server publishes rest endpoints for the user interface:

Login 
-------
http://localhost:8081/users/login

Method : POST

Header : Basic Authorization with credentials (admin/admin or user/user)

Result : It returns a session token, which should be used as authorization header value for rest of the calls.


Logout
-------

http://localhost:8081/users/logout

Method : POST

Header : Session token obtained from login request

Result : It returns a success message


ViewStatement
-------------

http://localhost:8081/users/viewStatements?id=1

Method : GET

Header : Session token obtained from login request

Parameters for Date range search : fromDate, toDate
Eg. http://localhost:8081/users/viewStatements?id=1&&fromDate=1/1/2012&&toDate=1/1/2022

Parameter for Amount range search : fromAmt, toAmt
Eg. http://localhost:8081/users/viewStatements?id=1&&fromAmt=8&&toAmt=100

Result: Json object with list of statement for particular account id



Build
-----
Note: In the AccountStatement project, Please make sure to change the "spring.datasource.url" value of jdbc:ucanaccess driver(line no 7) in the application.properties to point to the accountsdb.accdb DB file

-> mvn clean install (Do this for both AccountStatement and UserAccount projects)


Run 
-----

-> java -jar accountStatement-0.0.1-SNAPSHOT.jar (this runs on port 8080)

-> java -jar userInterface-0.0.1-SNAPSHOT.jar (this runs on port 8081)

