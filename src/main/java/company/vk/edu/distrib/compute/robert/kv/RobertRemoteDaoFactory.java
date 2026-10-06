package company.vk.edu.distrib.compute.robert.kv;

import java.io.IOException;

import company.vk.edu.distrib.compute.Dao;
import company.vk.edu.distrib.compute.kv.RemoteDaoFactory;
import company.vk.edu.distrib.compute.kv.RemoteDaoFactoryTest;

@RemoteDaoFactoryTest
public class RobertRemoteDaoFactory implements RemoteDaoFactory<String> {

    @Override
    public Dao<String> create(int... ports) throws IOException {
        if (ports.length < 1) {
            throw new IllegalArgumentException();
        }
        return new RobertRemoteDao(ports[0]);
    }
}
