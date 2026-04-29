package rodr.gq.repository;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import rodr.gq.model.Book;

import java.util.Optional;

@ApplicationScoped
public class BookRepository implements PanacheMongoRepository<Book> {
    
    public Optional<Book> findByIsbn(String isbn) {
        return find("isbn", isbn).firstResultOptional();
    }
}
