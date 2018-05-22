#!/bin/sh

./datagen.sh > /tmp/xDDD &
websocketd --port=8080 tail -f /tmp/xDDD

