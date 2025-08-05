package com.backend.ToDoList.repository.client;

import com.backend.ToDoList.dto.request.ExchangeTokenRequest;
import com.backend.ToDoList.dto.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "exchange-token", url = "https://oauth2.googleapis.com")
public interface GoogleExchangeToken {
    @PostMapping("/token")
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest req);
}
