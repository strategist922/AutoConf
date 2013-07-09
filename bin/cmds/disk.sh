#!/bin/bash
function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

iostat -d 1 | timestamp >> disk.log &
