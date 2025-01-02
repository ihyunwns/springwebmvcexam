package com.hyunwns.demoweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class BeanDefinitionTest {

    static String path = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";

    @Test
    public void 등록된_빈_찾기() throws Exception{
        //given
        ApplicationContext ac = new FileSystemXmlApplicationContext(path);

        String[] beanDefinitionNames = ac.getBeanDefinitionNames();

        for(String beanName : beanDefinitionNames){
            System.out.println(beanName);
        }

        //when

        //then
    }
}
