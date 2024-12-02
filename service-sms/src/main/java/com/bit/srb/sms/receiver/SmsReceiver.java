package com.bit.srb.sms.receiver;

import com.bit.srb.base.dto.SmsDTO;
import com.bit.srb.rabbitutil.constant.MQConst;
import com.bit.srb.sms.service.SmsService;
import com.bit.srb.sms.utli.SmsProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SmsReceiver {
    @Resource
    private SmsService smsService;

    // 消息发送时会自动被调用
    // 队列绑定器
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM),// 队列的名字
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO){
        log.info("SmsReceiver消息监听");
        Map<String,Object> param = new HashMap<>();
        param.put("code", smsDTO.getMessage());
        smsService.send(smsDTO.getMobile(), SmsProperties.TEMPLATE_CODE, param);
        ConcurrentHashMap<Object, Object> objectObjectConcurrentHashMap = new ConcurrentHashMap<>();
    }
}
