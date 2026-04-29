package rodr.gq.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Random;

@ApplicationScoped
public class InventoryService {
    private final Random random = new Random();

    public int getStock(String isbn) {
        try {
            Thread.sleep(50 + random.nextInt(50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return random.nextInt(10);
    }
}
