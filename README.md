# kasta-test


Test app for kasta - [Task](https://hackmd.io/@2QX6eXGqSJu70zPB776X9Q/rJdSetNhM?type=view)  

## Usage

start kafka on `localhost:9092`
> For start kafka an zookeeper can use docker-compose `docker-compose up`
 
After kafka started  
`lein ring server-headless`
> For booting app use `lein-ring` plugin. Handler setup in [project.cli](./project.cli) key `:ring`.
> By default start on PORT 3000


For test in IDEA can use  [project.cli](./rest-api.http) http client file. 


