AutoConf
========

AutoConf is a facility to auto-configure HiveQL queries transparently to the
programmer. It applies multiple configurations in a single query by classifying
the query stages and applying an appropriated configuration for each stage,
i.e., per-stage configuration.

Instalation
===========

Compile
-------

> $ git clone https://github.com/erlfilho/AutoConf.git

> $ cd AutoConf

> $ ant

Configure
---------

> $ vim autoconf.conf

> $ AUTOCONF\_HOME=_<path-to-autoconf>_

> $ vim conf/server.policy

In case you want to res

> codeBase "file:/path/to/AutoConf/-"

Execute
-------

> $ ./bin/autoconf-server

JavaDoc
-------

[JavaDoc](http://www.inf.ufpr.br/erlfilho/autoconf/)

License
-------

> Licensed to the Apache Software Foundation (ASF) under one
> or more contributor license agreements.  See the NOTICE file
> distributed with this work for additional information
> regarding copyright ownership.  The ASF licenses this file
> to you under the Apache License, Version 2.0 (the
> "License"); you may not use this file except in compliance
> with the License.  You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
