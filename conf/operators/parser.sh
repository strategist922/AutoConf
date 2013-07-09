#!/bin/bash
counter=1



while read line; do
   xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>
   <!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">
   <properties>"
   xml=$(echo -e "${xml}\n<entry key=\"groupid\">${counter}</entry>")
   xml=$(echo -e "${xml}\n<entry key=\"tuning\">/Users/Edson/Documents/Repositories/AutoConf/conf/tuning/tuning_${counter}.xml</entry>")
	for i in $line; do
		operator=$(awk -F',' '{print $1}' <<< ${i})
		value=$(awk -F',' '{print $2}' <<< ${i})
		xml=$(echo -e "${xml}\n<entry key=${operator}>${value}</entry>")
	done
	xml="${xml}</properties>"
	echo $xml >> group_${counter}
   xmllint --format group_${counter} > group_${counter}.xml
	rm group_${counter}
	counter=$((counter + 1))
done < a
