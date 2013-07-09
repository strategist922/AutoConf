#!/bin/bash
function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

sar 1 |timestamp >> cpu.log &
iostat -d 1 | timestamp >> disk.log &

if [ $(uname -o) == "Darwin" ]; then
	vm_stat 1 | timestamp >> mem.log &
else
	vmstat 1 | timestamp >> mem.log &
fi
