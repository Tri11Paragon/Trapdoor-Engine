#!/bin/bash

SOURCE_DIR="./src/"
DEST_DIR="./include/"

for txt_file in $(find "${SOURCE_DIR}" -type f -name "*.txt"); do
        new_path=$(dirname "$txt_file" | sed "s~${SOURCE_DIR}~${DEST_DIR}~")
        mkdir -p "${new_path}"
        mv "${txt_file}" "${new_path}"
done
