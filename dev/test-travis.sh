#!/bin/bash

set -e
set -x

FWDIR="$(cd "`dirname $0`"/..; pwd)"
cd "$FWDIR"

. ${FWDIR}/dev/travis-set-ver.sh

env | grep "CI_"

