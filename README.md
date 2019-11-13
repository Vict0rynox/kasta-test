# kasta-test


[Task](https://hackmd.io/@2QX6eXGqSJu70zPB776X9Q/rJdSetNhM?type=view)
Test app for kasta

## Usage

start kafka on `localhost:9092`
> For start kafka an zookeper can use dokcer-compose `docker-compose up`
 
After kafka started  
`lein ring server-headless`
> For booting app use `lein-ring` plugin. Handler setup in [project.cli](./project.cli) key `:ring`.
> By default start on PORT 3000


For test in IDEA can use `rest-api.http` http client file. 

## License

