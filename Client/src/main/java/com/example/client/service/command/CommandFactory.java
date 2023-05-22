package com.example.client.service.command;

import com.example.client.service.InnerCommand;
import com.example.client.util.SpringBeanFactory;
import com.example.common.enums.SystemCommandEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j(topic = "CommandFactory")
public class CommandFactory {
    public InnerCommand getInstance(String command) {

        Map<String, String> allClazz = SystemCommandEnum.getAllClazz();

        //兼容需要命令后接参数的数据 :q cross
        String[] trim = command.trim().split(" ");
        String clazz = allClazz.get(trim[0]);
        InnerCommand innerCommand;
        try {
            innerCommand = (InnerCommand) SpringBeanFactory.getBean(Class.forName(clazz));
        } catch (Exception e) {
            log.info("获取命令错误！");
            return null;
        }
        return innerCommand;
    }
}
