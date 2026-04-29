package rodr.gq.model;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;

@MongoEntity(collection = "books")
public class Book extends PanacheMongoEntity {
    public String title;
    public String author;
    public String isbn;
    public int year;

    public Book() {}

    public Book(String title, String author, String isbn, int year) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.year = year;
    }
}
