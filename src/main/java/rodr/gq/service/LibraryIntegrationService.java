package rodr.gq.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import rodr.gq.client.ReviewClient;
import rodr.gq.model.Book;
import rodr.gq.model.Review;
import rodr.gq.repository.BookRepository;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class LibraryIntegrationService {

    @Inject
    BookRepository bookRepository;

    @Inject
    ReviewClient reviewClient;

    @Inject
    InventoryService inventoryService;

    @Inject
    PriceService priceService;

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
