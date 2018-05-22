#!/bin/bash

# Count from 1 to 10 with a sleep
for ((COUNT = 1; COUNT <= 100; COUNT++)); do
  echo $(date +%s%N),$COUNT
  sleep 0.5
done
