#!/bin/bash

pkill -9 iostat

if [ $(uname -o) == "Darwin" ]; then
	pkill -9 vm_stat
else
	pkill -9 vmstat
fi

pkill -9 sar
pkill -9 mpstat
