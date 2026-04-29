package rodr.gq.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class PriceService {
    private final Random random = new Random();

    public double getPrice(String isbn) {
        try {
            Thread.sleep(50 + random.nextInt(50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 20.0 + random.nextDouble() * 50.0;
    }
}
