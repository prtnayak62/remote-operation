package com.ibm.autoconnect.controller;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.autoconnect.model.RemoteResponseModel;
import com.ibm.autoconnect.service.RemoteService;

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
    public ResponseEntity<?> remoteAc(@RequestBody List<String> data) {
        log.info("Received Data: " + data);
        RemoteResponseModel model = remoteService.processRemote(data);
        return new ResponseEntity<>(model, HttpStatusCode.valueOf(200));
    }
    
    @GetMapping("/ping")
    public String testPing() {
        return "Remote controller is working!";
    }
}
