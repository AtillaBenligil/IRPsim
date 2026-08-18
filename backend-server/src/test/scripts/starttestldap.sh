#!/bin/bash

# Dieses Skript startet einen lokalen LDAP-Container für Testläufe.
# Die Verbindungsdaten werden in einer Properties-Datei im angegebenen Ausgabeordner geschrieben.

function getPort {
  port=$(( 10389 ))
  quit=0

  while [ "$quit" -ne 1 ]; do
    netstat -na | grep $port >> /dev/null
    if [ $? -gt 0 ]; then
      quit=1
    else
      port=`expr $port + 1`
    fi
  done
  echo $port;
}

if [ "$#" -ne 2 ]; then
	echo "Es müssen der Pfad mit LDAP-Bootstrapdaten und der Ausgabeordner übergeben werden."
	exit 1
fi

resource_dir=$1
output_dir=$2

port=$(getPort)
name="TestLdap_$port"

mkdir -p "$output_dir"

echo "Starte $name auf $port"
echo $name > "$output_dir/ldap.txt"

docker run -d --name=$name \
	-p $port:389 \
	-e LDAP_ORGANISATION="IRPsim" \
	-e LDAP_DOMAIN="irpsim.local" \
	-e LDAP_ADMIN_PASSWORD="admin" \
	-e LDAP_TLS=false \
	-e LDAP_REMOVE_CONFIG_AFTER_SETUP=false \
	-v "$resource_dir:/container/service/slapd/assets/config/bootstrap/ldif/custom" \
	osixia/openldap:1.5.0

success=$?
if [ "$success" -ne 0 ]; then
	echo "LDAP-Container konnte nicht gestartet werden."
	exit $success
fi

sleep 10s

cat > "$output_dir/ldap.properties" <<EOF
IRPSIM_LDAP_URL=ldap://localhost:$port
IRPSIM_LDAP_BASE_DN=dc=irpsim,dc=local
IRPSIM_LDAP_BIND_DN=cn=admin,dc=irpsim,dc=local
IRPSIM_LDAP_BIND_PASSWORD=admin
IRPSIM_LDAP_USER_DN_PATTERN=uid=%s,ou=people
EOF
