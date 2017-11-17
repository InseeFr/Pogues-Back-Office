
sleep 2

curl -v http://admin:@localhost:9080/exist/rest/db/?_query=sm:passwd("admin", "admin")
