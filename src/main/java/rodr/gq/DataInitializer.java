package rodr.gq;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import rodr.gq.model.Book;
import rodr.gq.repository.BookRepository;

@ApplicationScoped
public class DataInitializer {

    @Inject
    BookRepository bookRepository;

    void onStart(@Observes StartupEvent ev) {
        if (bookRepository.count() == 0) {
            bookRepository.persist(new Book("The Java Virtual Machine", "James Gosling", "12345", 1996));
            bookRepository.persist(new Book("Clean Code", "Robert C. Martin", "67890", 2008));
        }
    }
}
