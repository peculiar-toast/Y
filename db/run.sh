docker run --name y-postgres -e POSTGRES_PASSWORD=mypass -e POSTGRES_USER=myuser -e POSTGRES_DB=y-db -p 8082:8082 -v y-postgres-data:/var/lib/postgresql/data -d postgres
