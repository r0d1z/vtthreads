package rodr.gq.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import rodr.gq.client.ReviewClient;
import rodr.gq.model.Book;
import rodr.gq.model.BookDetails;
import rodr.gq.model.Review;
import rodr.gq.repository.BookRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationScoped
public class LibraryService {

    private ExecutorService virtualThreadExecutor;

    @PostConstruct
    void init() {
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    @PreDestroy
    void cleanup() {
        if (virtualThreadExecutor != null && !virtualThreadExecutor.isShutdown()) {
            virtualThreadExecutor.shutdown();
        }
    }

    @Inject
    BookRepository bookRepository;

    @Inject
    ReviewClient reviewClient;

    @Inject
    InventoryService inventoryService;

    @Inject
    PriceService priceService;


    public BookDetails getBookFullDetails(String isbn) {
        var bookFuture = CompletableFuture.<Book>supplyAsync(() -> getBookWithRetry(isbn), virtualThreadExecutor);
        var reviewsFuture = CompletableFuture.<List<Review>>supplyAsync(() -> getReviewsWithFallback(isbn), virtualThreadExecutor);
        var stockFuture = CompletableFuture.<Integer>supplyAsync(() -> getStockWithTimeout(isbn), virtualThreadExecutor);
        var priceFuture = CompletableFuture.<Double>supplyAsync(() -> getPriceWithTimeout(isbn), virtualThreadExecutor);

        Book book = bookFuture.join();
        return new BookDetails(
            isbn,
            book.title,
            book.author,
            reviewsFuture.join(),
            stockFuture.join(),
            priceFuture.join()
        );
    }

    @Retry(maxRetries = 2, delay = 200, delayUnit = ChronoUnit.MILLIS, maxDuration = 800, durationUnit = ChronoUnit.MILLIS)
    @Timeout(value = 1000, unit = ChronoUnit.MILLIS)
    public Book getBookWithRetry(String isbn) {
        return bookRepository.findByIsbn(isbn)
            .orElse(new Book("Unknown", "Unknown", isbn, 0));
    }

    @Retry(maxRetries = 2, delay = 150, delayUnit = ChronoUnit.MILLIS, maxDuration = 600, durationUnit = ChronoUnit.MILLIS)
    @Timeout(value = 800, unit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "emptyReviews")
    public List<Review> getReviewsWithFallback(String isbn) {
        return reviewClient.getReviews(isbn);
    }

    public List<Review> emptyReviews(String isbn) {
        return Collections.emptyList();
    }

    @Retry(maxRetries = 2, delay = 100, delayUnit = ChronoUnit.MILLIS, maxDuration = 300, durationUnit = ChronoUnit.MILLIS)
    @Timeout(value = 400, unit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "defaultStock")
    public int getStockWithTimeout(String isbn) {
        return inventoryService.getStock(isbn);
    }

    public int defaultStock(String isbn) {
        return 0;
    }

    @Retry(maxRetries = 2, delay = 100, delayUnit = ChronoUnit.MILLIS, maxDuration = 300, durationUnit = ChronoUnit.MILLIS)
    @Timeout(value = 500, unit = ChronoUnit.MILLIS)
    @Fallback(fallbackMethod = "defaultPrice")
    public double getPriceWithTimeout(String isbn) {
        return priceService.getPrice(isbn);
    }

    public double defaultPrice(String isbn) {
        return 0.0;
    }
}
