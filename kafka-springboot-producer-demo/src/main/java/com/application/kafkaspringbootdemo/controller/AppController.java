package com.application.kafkaspringbootdemo.controller;

import com.application.kafkaspringbootdemo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AppController {

    @Value("${kafka.topic}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String,User> kafkaTemplate;
    
    @PostMapping("/data")
    public void home(@RequestBody User user){
        StringBuilder sb = new StringBuilder();
        final ListenableFuture<SendResult<String, User>> producer = kafkaTemplate.send(topicName, user);
        producer.addCallback(new ListenableFutureCallback<SendResult<String, User>>() {
            @Override
            public void onFailure(Throwable throwable) {
                sb.append(throwable.getMessage());
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(SendResult<String, User> result) {
                System.out.println("Data successfully registered with Kafka Topic..");
                System.out.println("Partition -> "+result.getRecordMetadata().partition());
                System.out.println("Offset -> "+result.getRecordMetadata().offset());
            }
        });
    }
}
