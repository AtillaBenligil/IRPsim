#!/bin/bash

# Stoppt den zuvor mit starttestldap.sh gestarteten LDAP-Container.

if [ "$#" -ne 1 ]; then
	echo "Es muss der Ausgabeordner übergeben werden."
	exit 1
fi

output_dir=$1
name=`cat "$output_dir/ldap.txt"`

docker stop $name

name2=$name
rename=1
count=0
while [[ "$rename" -ne 0 && "$count" -le 100 ]]; do
  name2=$name"_old_"$count
  echo "Benne um zu: $name2"
  docker rename $name $name2
  rename=$?
  echo "Rename: $rename $count"
  count=`expr $count + 1`
done
echo "LDAP umbenennen abgeschlossen"
