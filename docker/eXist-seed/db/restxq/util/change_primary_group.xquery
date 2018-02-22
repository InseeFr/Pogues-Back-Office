xquery version "3.0";

for $user in ("batch","orbeon","pilotage","coltrane","portail")
return sm:set-user-primary-group($user,"appli")