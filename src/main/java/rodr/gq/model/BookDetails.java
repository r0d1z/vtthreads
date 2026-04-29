package rodr.gq.model;

import java.util.List;

public record BookDetails(
    String isbn,
    String title,
    String author,
    List<Review> reviews,
    int stock,
    double price
) {}
