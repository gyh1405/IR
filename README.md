# IR Lucene Search Engine Setup


## Prerequisite
*   [`Link`](https://www.python.org/downloads/) Python version >3.11
*   [`Link`](https://scrapy.org) Scrapy
*   [`Link`](https://www.oracle.com/sg/java/technologies/downloads/) Java JDK
*   [`Link`](https://nodejs.org/en) NodeJS


## To use Scrapy Crawler

```
scrapy runspider encyspider.py
```


## To Start Backend (Spring Boot Server)
Before Running the Spring Boot Server, at the `application.properties` change the directory of saved index.
Remember to add META-INF, `additional-spring-configuration-metadata.json`

Open Eclipse, run `DemoApplication.java` as Spring boot app.

Server at `http://localhost:8080/`

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

## Our Demo Video

[`Demo Video`](https://youtu.be/bSFtxAKH-J4)
