# IR
 IR Project from School.

 This Search engine Project is built upon NextJS 13, Apache Nutch™, Apache Solr™ and Apache Zookeeper™. 
 
[Apache Nutch™](https://nutch.apache.org) is a production ready Web Crawler, which is:
*   Scalable: Relying on Apache Hadoop™ data structures, it can batch process large data volumes, and can also be tailored to smaller jobs.
*   Pluggable: Offer powerful plugins i.e., parsing with Apache Tika™, indexing with Apache Solr™, Elasticsearch and more.
*   Extensible: Provides intuitive and stable interfaces for popular functions i.e., Parsers, HTML Filtering, Indexing and Scoring for custom implementations.

 [Apache Solr™](https://solr.apache.org) is search platform built on Apache Lucene™.

 [Apache Zookeeper™](https://zookeeper.apache.org) is a centralized service for maintaining configuration info, naming, providing distributed synchronization, and group services. It can manage different nodes cluster created in Solr. To start Solr Cloud will requires ZooKeeper.

## Prerequisite
*   [`Link`](https://www.oracle.com/sg/java/technologies/downloads/) Java JDK
*   [`Link`](https://nodejs.org/en) NodeJS


## To Start Backend
In VSCode Terminal, change directory to Apache ZooKeeper Folder

```
cd Backend/apache-zookeeper-3.7.1-bin/
```

Start ZooKeeper Server

```
bin/zkServer.sh start
```

Change directory to Solr Folder

```
cd ..
```
```
cd solr-8.11.2/
```

Start Solr Cloud

```
bin/solr start -cloud
```

View Solr Admin at: `http://localhost:8983/solr/#/`

## To Start Frontend
Change directory to "gui" folder

```
cd gui/
```

Install all the required Node Modules

```
npm install
```

Start Next JS

```
npm run dev
```

View the GUI at: `http://localhost:3000`