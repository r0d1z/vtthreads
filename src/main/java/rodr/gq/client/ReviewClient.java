package rodr.gq.client;

import jakarta.enterprise.context.ApplicationScoped;
import rodr.gq.model.Review;

import java.util.List;
import java.util.Random;

@ApplicationScoped
public class ReviewClient {

    private final Random random = new Random();

    public List<Review> getReviews(String isbn) {
        // Simulate HTTP request latency
        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return List.of(
            new Review(isbn, "Alice", "Great book!", 5),
            new Review(isbn, "Bob", "Informative but a bit long.", 4)
        );
    }
}
