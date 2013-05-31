#!/bin/bash
function timestamp {
  while read line; do
    echo "$(date '+%d-%m-%y %H:%M:%S') $line"
  done
}

sar 1 |timestamp >> cpu.log &
