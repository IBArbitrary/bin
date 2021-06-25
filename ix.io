#!/usr/bin/env bash

URL="ix.io"

if [ $# -eq 0 ]; then
    echo -e "Usage: ix.io FILE\n"
    exit 1
fi

FILE=$1

if [ ! -f "$FILE" ]; then
    echo "File ${FILE} not found"
    exit 1
fi

RESPONSE=$(curl -# -F "f:1=@${FILE}" "${URL}")

echo "${RESPONSE}" | xclip -selection clipboard
echo "${RESPONSE}"
