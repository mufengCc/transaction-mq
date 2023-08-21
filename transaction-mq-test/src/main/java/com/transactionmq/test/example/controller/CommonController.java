package com.transactionmq.test.example.controller;

import com.transactionmq.test.example.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @GetMapping(value = "/sendMessage")
    public void sendMessage() {
        commonService.sendMessage();
    }

    @GetMapping(value = "/sendMessageNoTran")
    public void sendMessageNoTran() {
        commonService.sendMessageNoTran();
    }

    @GetMapping(value = "/sendMessageRollback")
    public void sendMessageRollback() {
        commonService.sendMessageRollback();
    }

    @GetMapping(value = "/messageRetry")
    public void messageRetry() {
        commonService.messageRetry();
    }

}
