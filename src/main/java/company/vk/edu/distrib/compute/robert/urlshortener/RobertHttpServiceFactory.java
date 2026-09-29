package company.vk.edu.distrib.compute.robert.urlshortener;

import company.vk.edu.distrib.compute.AbstractHttpServiceFactory;
import java.io.IOException;

public class RobertHttpServiceFactory extends AbstractHttpServiceFactory<RobertUrlShortenerService> {

    @Override
    protected RobertUrlShortenerService doCreate(int port) throws IOException {
        return new RobertUrlShortenerService(port);
    }

}
