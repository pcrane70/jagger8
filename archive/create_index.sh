#!/bin/bash

# Empty file
echo " " > index.html

# Fill index for versions
echo "<!DOCTYPE html>" >> index.html
echo "<html>" >> index.html
echo "<body>" >> index.html

echo "<a href=\"../doc/index.html\">current version</a><br/>" >> index.html

# Search for directories with index.html
find . -maxdepth 2 -mindepth 2 -name 'index.html' | while read line; do
    # Extract name of directory
    name=$(echo $line | cut -d'/' -f 2)
    echo "Name '$name' with value '$line'"
    # Make links to these files
    echo "<a href=$line>$name</a><br/>" >> index.html
#    cp -- "$line" /tmp
done

echo "</body>" >> index.html
echo "</html>" >> index.html