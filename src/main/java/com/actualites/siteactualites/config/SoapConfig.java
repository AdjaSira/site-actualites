package com.actualites.siteactualites.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.logging.Logger;

@EnableWs
@Configuration
public class SoapConfig extends WsConfigurerAdapter {

    private static final Logger LOGGER = Logger.getLogger(SoapConfig.class.getName());

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        LOGGER.info("Initialisation de MessageDispatcherServlet pour /ws/*");
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "users")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema usersSchema) {
        LOGGER.info("Création du bean DefaultWsdl11Definition pour 'users'");
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UserServicePortType");
        wsdl11Definition.setLocationUri("/ws/users");
        wsdl11Definition.setTargetNamespace("http://siteactualites.com/soap/users");
        wsdl11Definition.setSchema(usersSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema usersSchema() {
        LOGGER.info("Chargement du schéma XSD depuis xsd/users.xsd");
        return new SimpleXsdSchema(new ClassPathResource("xsd/users.xsd"));
    }
}