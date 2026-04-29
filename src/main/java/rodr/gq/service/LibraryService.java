package rodr.gq.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;
import rodr.gq.model.Book;
import rodr.gq.model.BookDetails;
import rodr.gq.model.Review;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class LibraryService {

    @Inject
    LibraryIntegrationService integrationService;

    @Inject
    ManagedExecutor managedExecutor;

    public BookDetails getBookFullDetails(String isbn) {
        // ManagedExecutor handles context propagation (Security, RequestScope, etc.)
        // We configure it to use virtual threads in application.properties
        
        var bookFuture = CompletableFuture.supplyAsync(
            () -> integrationService.getBookWithRetry(isbn), managedExecutor);
        
        var reviewsFuture = CompletableFuture.supplyAsync(
            () -> integrationService.getReviewsWithFallback(isbn), managedExecutor);
        
        var stockFuture = CompletableFuture.supplyAsync(
            () -> integrationService.getStockWithTimeout(isbn), managedExecutor);
        
        var priceFuture = CompletableFuture.supplyAsync(
            () -> integrationService.getPriceWithTimeout(isbn), managedExecutor);

        // Blocking here is fine because this method itself is running on a Virtual Thread 
        // (due to @RunOnVirtualThread in the Resource)
        Book book = bookFuture.join();
        List<Review> reviews = reviewsFuture.join();
        int stock = stockFuture.join();
        double price = priceFuture.join();

        return new BookDetails(
            isbn,
            book.title,
            book.author,
            reviews,
            stock,
            price
        );
    }
}
