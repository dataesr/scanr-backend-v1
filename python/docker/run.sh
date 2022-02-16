#!/bin/sh
. /appenv/bin/activate

export LC_ALL=en_US.UTF-8

python3 $MODULE/main.py $*

