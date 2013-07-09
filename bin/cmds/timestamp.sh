function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

