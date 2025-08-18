package ibm.newgen.mobility.remoteOperation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibm.newgen.mobility.remoteOperation.model.RemoteResponseModel;
import ibm.newgen.mobility.remoteOperation.service.RemoteService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/remote")
@Slf4j
public class RemoteOperationController {

    private final RemoteService remoteService;

    public RemoteOperationController(RemoteService remoteService) {
        this.remoteService = remoteService;
    }


    @PostMapping("/remoteac")
    public ResponseEntity<?> remoteAc(@RequestBody Map<String, Object> data) {
        log.info("Received Data: " + data);
        RemoteResponseModel model = remoteService.processRemote(data);
        return new ResponseEntity<>(model, HttpStatusCode.valueOf(200));
    }
    
    @GetMapping("/ping")
    public String testPing() {
        return "Remote controller is working!";
    }
}
