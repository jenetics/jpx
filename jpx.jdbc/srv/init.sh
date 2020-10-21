echo "initialize preconditions for container and then starts it"
echo "checking if work directory exists"

if [ ! -d "$PWD/var" ]; then
  echo "no working directory, preparing directory structure"
  mkdir -pv var/dbdata
  mkdir -pv var/lib-pgadmin

  echo "creating empty server.json"
  touch var/server.json

  echo "list directory structure"
  ls -R
fi
