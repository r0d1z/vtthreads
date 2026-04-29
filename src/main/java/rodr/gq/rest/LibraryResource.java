package rodr.gq.rest;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import rodr.gq.model.BookDetails;
import rodr.gq.service.LibraryService;

@Path("/library")
public class LibraryResource {

    @Inject
    LibraryService libraryService;

    @GET
    @Path("/book/{isbn}")
    @Produces(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public BookDetails getBook(@PathParam("isbn") String isbn) {
        return libraryService.getBookFullDetails(isbn);
    }
}
