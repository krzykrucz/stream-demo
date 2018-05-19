#!/bin/sh

run_spark='BROKERS="localhost:9092" TOPICS="my-source" mvn install exec:java -pl spark-processor' # '-pl spark-processor' pozwala z katalogu głównego zawołać moduł sparkowu
#TODO: uzupełnić komendy:
run_source='echo Uruchamiam źródło && sleep 3 && echo xd'
run_sink='echo Uruchamiam zlew'
run_flink='echo uruchamiam flinka'

print_help(){
    echo "$0 (start|stop) (source|sink|spark|flink)"
}

if [ "$#" -lt 2 ]; then
    print_help
    exit 1
fi

run_command(){
    if [ -f /tmp/"$1" ]; then
        if ps -p $(cat /tmp/"$1") > /dev/null 
        then
            echo "Już uruchomione! Najpierw zastopuj"
            exit 2
        fi # else process was terminated some other way and we didn't notice
    fi
    sh -c "$1" &
    echo $! > /tmp/"$1" # save PID
}

kill_command(){
    if [ ! -f /tmp/"$1" ]; then
        echo "Proces już nie żyje"
        exit 7
    fi
    pkill -P $(cat /tmp/"$1") # that file contains PID
    rm /tmp/"$1"
}

get_command(){
    if [ "$1" = 'source' ]; then
        cmd="$run_source"
    elif [ "$1" = 'sink' ]; then
        cmd="$run_sink"
    elif [ "$1" = 'spark' ]; then
        cmd="$run_spark"
    elif [ "$1" = 'flink' ]; then
        cmd="$run_flink"
    else
        print_help
        exit 3
    fi
}

get_command "$2"

if [ "$1" = 'start' ]; then
    run_command "$cmd"
elif [ "$1" = 'stop' ]; then
    kill_command "$cmd"
else
    print_help
    exit 4
fi

