Flink Core
==============================================================================

There's a lot going on in Flink core! 
This module creates a Flink job that can exercise a lot of the functions and techniques in the core flink libraries.

The job is packaged in an uber-jar containing _most_ but not _all_ of the dependencies necessary to run the job in a Flink cluster.

| How do I...                                | Examples                                                                                                                |
|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| Print out info about the local environment | [com.skraba.flink.enchiridion.core.LocalInfoTask](src/main/scala/com/skraba/flink/enchiridion/core/LocalInfoTask.scala) |

Running the driver
------------------------------------------------------------------------------

This project includes an [example executable](src/main/scala/com/skraba/flink/enchiridion/core/FlinkJobGo.scala)
that can be run from the command line to execute a job Flink jobs.

```bash
mvn package

# Using the uber jar from the command line
alias flink_core_go="java -jar $(find ~+ -name flink-enchiridion-core-\*.jar)"
flink_core_go --help

# The simple info job
flink_core_go info --help
flink_core_go info
```