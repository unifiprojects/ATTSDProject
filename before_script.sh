#!/bin/bash

set -ev
if [ "${TRAVIS_PULL_REQUEST}" = "true" ]; then
	sh -e /etc/init.d/xvfb start
fi