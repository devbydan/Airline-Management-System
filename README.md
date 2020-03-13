<h1 align="center">Welcome to Airline Management System 👋</h1>
<p>
</p>


> Airline Management System written in PSQL with a Java-based interface. This system is used to track information about different airlines, the planes they own, the maintenance of those planes, the pilots they employ and the trips that the pilots make, as well as information about the customers that use the airline services.

## Author

👤 **Dan Murphy**

* Website: https://www.linkedin.com/in/cs-dan-murphy/
* Github: [@pseudodan](https://github.com/pseudodan)

## Testing The Application

AMS requires [PSQL](https://www.postgresql.org/download/) and a [JRE](https://www.java.com/en/download/) to run.

Clone the project [here](https://github.com/pseudodan/Airline-Management-System.git) and enter its respective directory.

Install the dependencies prior to running the application.

Next, we need to start and create a PSQL instance.

```sh
$ cd postgresql
$ source startPostgreSQL.sh
$ source createPostgreSQL.sh
```

Now that the server instance is started and created, proceed to enter the ```java/src``` dir

```sh
$ cd java/src
$ source compile.sh
```

The application will now be running in your terminal with a text-based menu. 



***
