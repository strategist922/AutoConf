#!/bin/bash
function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

# network
sar -n DEV 1 |timestamp >> net.log &

# cpu
sar 1 |timestamp >> cpu.log &

# disk
iostat -d 1 | timestamp >> disk.log &

# mem
if [ $(uname -o) == "Darwin" ]; then
	vm_stat 1 | timestamp >> mem.log &
else
	vmstat 1 | timestamp >> mem.log &
fi
