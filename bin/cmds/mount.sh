#!/bin/bash

# udisks --unmount /dev/sda2 2> /dev/null 1>/dev/null
# rm ramiro 2> /dev/null 1>/dev/null
# DEVICE=$(udisks --mount /dev/sda2 | awk '{print $4}')
# mkdir $DEVICE/c3sl/ramiro
# ln -s "$DEVICE/c3sl/ramiro" ramiro


source hadoopctl.conf 
echo -ne "\n * Mouting $HOSTNAME ... "

# Check if device exists
if test ! -e ${REMOTE_MOUNT_DEVICE}; then 
    echo  -ne " Device ${REMOTE_MOUNT_DEVICE} not found at $HOSTNAME."
    exit -1
fi

# mount device and store it's name
counter=0;
DEVICE=$(udisks --mount $REMOTE_MOUNT_DEVICE | awk '{print $4}')
while test ${DEVICE} == "is"; do
    udisks --unmount $REMOTE_MOUNT_DEVICE 2>/dev/null 1>/dev/null
    DEVICE=$(udisks --mount $REMOTE_MOUNT_DEVICE | awk '{print $4}')
    counter=$(echo $counter + 1| bc -l)
    if test $counter -gt 5; then
        echo -ne " Error: Could not mount device." 
        exit -1;
    fi
done
echo -ne "\e[0;34m mounted \e[0m"

# create mount dir if does not exist
if test ! -d "${DEVICE}/${REMOTE_MOUNT_DIR}"; then
    mkdir ${DEVICE}/${REMOTE_MOUNT_DIR}
fi

if test -d $REMOTE_MOUNT_LINK; then 
    DATE=$(date +%Hh%Mm%S)
    mv ${REMOTE_MOUNT_LINK} "${REMOTE_MOUNT_LINK}_$DATE"
fi

# create new link
if test -L ${REMOTE_MOUNT_LINK}; then
    rm ${REMOTE_MOUNT_LINK} 
fi
ln -s "$DEVICE/$REMOTE_MOUNTDIR" ${REMOTE_MOUNT_LINK}


