package company.vk.edu.distrib.compute.robert.kv;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Path;

import com.sun.net.httpserver.HttpServer;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.kv.KVService;
import company.vk.edu.distrib.compute.robert.api.v0.StatusHandler;
import company.vk.edu.distrib.compute.robert.dao.RobertByteDao;
import company.vk.edu.distrib.compute.robert.kv.api.v0.EntityHandler;
import company.vk.edu.distrib.compute.robert.kv.validation.implementations.KVValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RobertKVService implements KVService {
    private static final Logger log = LoggerFactory.getLogger(RobertKVService.class);

    private final int port;
    private final Dao<byte[]> dao;
    private final HttpServer httpServer;
    private ServiceState state = ServiceState.NEW;

    public RobertKVService(int initPort) throws IOException {
        port = initPort;

        Path storageRoot = Path.of(
            System.getProperty("java.io.tmpdir"),
            "vkedu-kv-robert",
            Integer.toString(port)
        );
        dao = new RobertByteDao(storageRoot, "entities", new KVValidator());
        httpServer = HttpServer.create();
        log.atDebug().log("Created unbound KV HTTP server");
    }

    private void initContexts() {
        httpServer.createContext(StatusHandler.PATH, new StatusHandler());
        httpServer.createContext(EntityHandler.PATH, new EntityHandler(dao));
    }

    @Override
    public synchronized void start() {
        if (state != ServiceState.NEW) {
            throw new IllegalStateException();
        }

        state = ServiceState.STARTED;
        initContexts();
        try {
            httpServer.bind(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), port),
                0
            );
            httpServer.start();
            log.atInfo().log("KV service started on {}", httpServer.getAddress());
        } catch (IOException e) {
            state = ServiceState.STOPPED;
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized void stop() {
        if (state != ServiceState.STARTED) {
            throw new IllegalStateException();
        }

        httpServer.stop(1);
        state = ServiceState.STOPPED;
        closeDao();
        log.atInfo().log("KV service stopped");
    }

    private void closeDao() {
        try {
            dao.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private enum ServiceState {
        NEW,
        STARTED,
        STOPPED
    }
}
