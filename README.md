# jvm-metrics-ws
A prototype RESTful Web Service exposing JVM metrics. If it takes off I'll refactor/add tests later. 


#Usage
1. Navigate to the root of the project and run "activator run". You will need to have Play installed.

#API
###GET /jvm/:host

where host is either alias (need to have previously used PUT /jvm) or the ip and port of the application exposing JMX.

{
    "os": {
        "name": "Linux",
        "arch": "amd64",
        "processors": 2,
        "version": "2.6.32-504.12.2.el6.x86_64"
    },
    "heap": {
        "init": 536870912,
        "used": 336193488,
        "commited": 603979776,
        "max": 670892032
    },
    "thread": {
        "count": 120
    },
    "timestamp": 24834582890583
}

###GET /ws/jvm/:host

WebSockets API. JVM state is continously pushed to the client.

See /jvm/:host for response.



### GET /jvm 
{
    "hosts": [
        {
            "alias": "weatherApp",
            "address": "10.1.28.36:7214"
        }
    ]
}


### PUT /jvm 
[{"alias": "weatherApp", "address":"10.1.28.36:7214"}]
