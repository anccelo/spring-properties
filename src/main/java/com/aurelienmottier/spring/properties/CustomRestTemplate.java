package com.aurelienmottier.spring.properties;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("GDNRestTemplate")
public class CustomRestTemplate extends RestTemplate {

}