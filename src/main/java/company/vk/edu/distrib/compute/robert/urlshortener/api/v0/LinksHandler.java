package company.vk.edu.distrib.compute.robert.urlshortener.api.v0;

import java.io.IOException;
import java.util.NoSuchElementException;

import company.vk.edu.distrib.compute.robert.urlshortener.api.models.AbstractHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.HttpStatus;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.Request;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.Response;
import company.vk.edu.distrib.compute.robert.urlshortener.dao.RobertDao;
import company.vk.edu.distrib.compute.robert.urlshortener.utils.IdGeneratorUtils;

// * Во всех случаях, когда передаётся либо не валидный `<ID>` либо невалидная ссылка в теле запроса (POST/PUT методы)
// - надо вернуть `422 Unprocessable Content`
public class LinksHandler extends AbstractHandler {
    public static final String PATH = "/v0/links";
    private static final int ID_LENGTH = 10;
    
    private final RobertDao dao;
    private final int port;

    public LinksHandler(RobertDao inputDao, int inputPort) {
        super();
        dao = inputDao;
        port = inputPort;
    }
    
    // * `POST /v0/links` -- создать короткую ссылку для заданной в теле, `Content-Type: text/html; charset=utf-8`. 
    // Возвращает `201 Created`, `Content-Type: text/html; charset=utf-8` и короткую ссылку в теле. 
    @Override
    public Response post(Request request) throws NoSuchElementException, IOException {
        String longLink = request.bodyAsString();
       
        boolean shouldRetry = true;
        String id = "";
        while (shouldRetry) {
            id = IdGeneratorUtils.randomID(ID_LENGTH);
            shouldRetry = !dao.create(id, longLink);
        }

        return Response.builder()
            .setStatus(HttpStatus.CREATED.code())
            .putHeader("Content-Type", "text/html; charset=utf-8")
            .setBody("http://localhost:%s/%s".formatted(port, id))
            .build();
    }
}
