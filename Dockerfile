FROM tomcat:8.5.16-jre8

RUN rm -rf $CATALINA_HOME/webapps/*
ADD src/main/resources/log4j2.xml $CATALINA_HOME/webapps/log4j2.xml
ADD ./target/*.war $CATALINA_HOME/webapps/ROOT.war