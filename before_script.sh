#!/bin/bash

set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "true" ]; then
	export DISPLAY:=99.0
	sh -e /etc/init.d/xvfb start
fi