package ibm.newgen.mobility.remoteOperation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ibm.newgen.mobility.*")
public class RemoteOperation {
    
    public static void main(String[] args) {
        SpringApplication.run(RemoteOperation.class, args);
    }
}
