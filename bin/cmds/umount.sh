#!/bin/bash

# udisks --unmount /dev/sda2
# udisks --unmount /dev/sda2 2> /dev/null

source hadoopctl.conf
echo -ne " \n * Unmouting $HOSTNAME ... "

# Check if device exists
if test ! -e ${REMOTE_MOUNT_DEVICE}; then 
    echo " Device ${REMOTE_MOUNT_DEVICE} not found at $HOSTNAME."
    exit -1
fi

pkill java

udisks --unmount $REMOTE_MOUNT_DEVICE && {
    echo -ne "\e[0;31m unmounted \e[0m"
}

# if test -L $REMOTE_MOUNT_LINK; then
#    DATE=$(date +%Hh%Mm%S)
#    mv ${REMOTE_MOUNT_LINK} "${REMOTE_MOUNT_LINK}_$DATE"
# fi

if test -L $REMOTE_MOUNT_LINK; then
    rm $REMOTE_MOUNT_LINK && {
        echo -ne " *** $REMOTE_MOUNT_LINK has been removed. "
    }
fi
