The Flink Enchiridion
==============================================================================

![Java CI](https://github.com/RyanSkraba/flink-enchiridion/workflows/Java%20CI/badge.svg)

️🏠[Apache Flink®][Home] 📓[Github] 🚩[Issues] 🧑‍🤝‍🧑[People] 📚[Wiki]

_[**Enchiridion**](https://en.wikipedia.org/wiki/Enchiridion): **A small manual or handbook.**_  It's a bit like a tech [cook book](https://www.oreilly.com/search/?query=cookbook), but a bigger, fancier, SEO-optimizabler word.

<!-- 2020/05/25: 920 O'Reilly results
     2020/06/05: 4758 O'Reilly results (but changed the search URL)
     2020/07/30: 5043 O'Reilly results
     2022/01/25: 5164 O'Reilly results
     2023/01/09: 6228 O'Reilly results -->

[Apache Flink][Home] is a big data, streaming execution engine that can be used to execute data processing jobs.
This project describes how to do many common tasks using Flink.

[Home]: https://flink.apache.org
[Github]: https://github.com/apache/flink
[Issues]: https://issues.apache.org/jira/projects/FLINK
[People]: https://projects.apache.org/committee.html?flink
[Wiki]: https://cwiki.apache.org/confluence/display/FLINK

Modules
------------------------------------------------------------------------------

| module                 | description                                                     |
|------------------------|-----------------------------------------------------------------|
| [core](core/readme.md) | Unit tests and example jobs for the Flink Core Java SDK.        |
| [delta](delta/       ) | Unit tests and example jobs for connecting Flink to Delta Lake. |

Building the project
------------------------------------------------------------------------------

```bash
mvn spotless:apply clean package
```
