package cc.eugen.backend.rest.proxy.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.webservices.client.HttpWebServiceMessageSenderBuilder;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Configuration
public class WebserviceClientConfiguration {


    @Value("${backend.url}")
    private String wsdlUrl;

    @Bean
    public WebServiceTemplate webServiceTemplate(WebServiceTemplateBuilder webServiceTemplateBuilder) {
        return webServiceTemplateBuilder
                .messageSenders(httpComponentsMessageSender())
                .setDefaultUri(wsdlUrl)
                .setMarshaller(marshaller())
                .setUnmarshaller(marshaller())
                .build();
    }


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("cc.eugen.backend.ws.generated");
        return marshaller;
    }

    @Bean
    public WebServiceMessageSender httpComponentsMessageSender() {

        return new HttpWebServiceMessageSenderBuilder()
                .setConnectTimeout(Duration.of(100, ChronoUnit.MILLIS))
                .setReadTimeout(Duration.of(1500, ChronoUnit.MILLIS)).build();

    }
}
