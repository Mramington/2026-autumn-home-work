package company.vk.edu.distrib.compute.robert.urlshortener.api.v0;

import company.vk.edu.distrib.compute.robert.urlshortener.api.models.AbstractHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.HttpStatus;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.Request;
import company.vk.edu.distrib.compute.robert.urlshortener.api.models.Response;

public class StatusHandler extends AbstractHandler {
    public static final String PATH = "/v0/status";

    // * `GET /v0/status` -- `200` в нормальной ситуации, `503` в случае проблем.
    @Override
    public Response get(Request request) {
        return Response.builder()
            .setStatus(HttpStatus.OK.code())
            .build();
    }
}
