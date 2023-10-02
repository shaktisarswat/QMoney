package com.javaproject.stock.portfolio;


import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

    // Implement the method to return new instance of PortfolioManager.
    // Remember, pass along the RestTemplate argument that is provided to the new instance.
    public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {
        return new PortfolioManagerImpl(restTemplate);
    }
}
