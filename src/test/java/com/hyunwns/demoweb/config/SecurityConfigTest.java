package com.hyunwns.demoweb.config;

import com.hyunwns.demoweb.dto.SignUpDTO;
import com.hyunwns.demoweb.repository.MemberRepository;
import com.hyunwns.demoweb.service.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {


    static String path = "C:\\Users\\ihyun\\Desktop\\springmvcwebexam\\src\\main\\webapp\\WEB-INF\\spring\\appServlet\\servlet-context.xml";
    ApplicationContext anno_ac;
    ApplicationContext xml_ac;
    @BeforeEach
    void setUp() {
        anno_ac = new AnnotationConfigApplicationContext(SecurityConfig.class);
        xml_ac = new FileSystemXmlApplicationContext(path);
    }


    @Test
    public void 빈_등록_확인() throws Exception{
        //given
//        String[] beanDefinitionNames = anno_ac.getBeanDefinitionNames();
//        for (String beanDefinitionName : beanDefinitionNames) {
//            Object bean = anno_ac.getBean(beanDefinitionName);
//            System.out.println("어노테이션 빈 이름 : " + beanDefinitionName + " object: " + bean);
//        }


        String[] beanDefinitionNames2 = xml_ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames2) {
            Object bean = xml_ac.getBean(beanDefinitionName);
            System.out.println("XML 빈 이름 : " + beanDefinitionName + "object: " + bean);
        }

        //when

        //then
    }

}