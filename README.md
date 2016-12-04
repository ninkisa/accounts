Prerequisites:
Tomcat 8
MySQL server

1. unpack accounts.zip
2. go to unpacked folder and build the project using maven
Command:
	C:\accounts>mvn clean install
3. create folder "conifg" under Tomcat installation folder
4. create application.cfg under the config folder and enter the following properties

jdbc.url=jdbc:mysql://<mysql host>:<port>/<dbname>
jdbc.user=<DB User>
jdbc.password=<DB Password>

5. copy the generated accounts.war file under the webapps folder in the tomcat installation
6. start tomcat 
7. access the application on the following URL
http://<host>:<port>/accounts/



Example for application.cfg file
jdbc.url=jdbc:mysql://localhost:3306/accounts
jdbc.user=demoUser
jdbc.password=abcd1234
