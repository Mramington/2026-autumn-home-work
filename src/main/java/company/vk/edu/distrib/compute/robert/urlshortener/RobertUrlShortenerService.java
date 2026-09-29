package company.vk.edu.distrib.compute.robert.urlshortener;

import company.vk.edu.distrib.compute.robert.urlshortener.api.models.AuthHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.v0.ForwardHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.v0.InternalUserHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.v0.LinksForwardHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.v0.LinksHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.api.v0.StatusHandler;
import company.vk.edu.distrib.compute.robert.urlshortener.dao.RobertDao;
import company.vk.edu.distrib.compute.robert.urlshortener.validation.implementations.UrlValidator;
import company.vk.edu.distrib.compute.robert.urlshortener.validation.implementations.UserValidator;
import company.vk.edu.distrib.compute.urlshortener.UrlShortenerService;
import edu.umd.cs.findbugs.annotations.Nullable;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobertUrlShortenerService implements UrlShortenerService {
    private static final Logger log = LoggerFactory.getLogger(RobertUrlShortenerService.class);

    private final int port;
    private final RobertDao linksDao;
    private final RobertDao usersDao;

    @Nullable
    private HttpServer httpServer;    
    
    public RobertUrlShortenerService(int initPort) throws IOException {
        port = initPort;

        Path storageRoot = Path.of(
            System.getProperty("java.io.tmpdir"),
        "vkedu-urlshortener-robert"
        );

        UrlValidator urlValidator = new UrlValidator();
        UserValidator userValidator = new UserValidator();
        linksDao = new RobertDao(storageRoot, "links", urlValidator);
        usersDao = new RobertDao(storageRoot, "users", userValidator);

        initServer();
    }

    private void initServer() throws IOException {
        httpServer = HttpServer.create();

        httpServer.createContext(ForwardHandler.PATH, new ForwardHandler(linksDao));
        httpServer.createContext(LinksHandler.PATH, new AuthHandler(new LinksHandler(linksDao, port), usersDao));
        httpServer.createContext(StatusHandler.PATH, new StatusHandler());
        httpServer.createContext(InternalUserHandler.PATH, new InternalUserHandler(usersDao));
        httpServer.createContext(
            LinksForwardHandler.PATH, 
            new AuthHandler(
                new LinksForwardHandler(linksDao),
                usersDao
            )
        );
        
        log.atDebug().log("Created unbound HTTP server");
    }

    @Override
    public void start() {
        if (httpServer.getAddress() == null) {
            try {
                httpServer.bind(
                    new InetSocketAddress(
                        InetAddress.getLoopbackAddress(),
                        port
                    ),
                    0
                );
                httpServer.start();
                log.atInfo().log("Service started on {}", httpServer.getAddress());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            throw new IllegalStateException("HTTP server has already been started");
        }
    }

    @Override
    public void stop() {
        httpServer.stop(1);
        log.atInfo().log("Service stopped");
    }
}
