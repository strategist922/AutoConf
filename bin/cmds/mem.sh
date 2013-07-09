#!/bin/bash
function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

if [ $(uname -o) == "Darwin" ]; then
	vm_stat 1 | timestamp >> mem.log &
else
	vmstat 1 | timestamp >> mem.log &
fi
