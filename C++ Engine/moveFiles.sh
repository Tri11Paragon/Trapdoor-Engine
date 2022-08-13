#!/bin/bash

SOURCE_DIR="./src"
HEADER_DIR="./include"

for txt_file in $(find "${SOURCE_DIR}" -type f -name "*.h"); do
        new_path=$(dirname "$txt_file" | sed "s~${SOURCE_DIR}~${HEADER_DIR}~")
        mkdir -p "${new_path}"
        mv "${txt_file}" "${new_path}"
done

for txt_file in $(find "${HEADER_DIR}" -type f -name "*.cpp"); do
        new_path=$(dirname "$txt_file" | sed "s~${HEADER_DIR}~${SOURCE_DIR}~")
        mkdir -p "${new_path}"
        mv "${txt_file}" "${new_path}"
done

#find "${SOURCE_DIR}" -type d -empty -delete
