package ru.arkhipov.MySecondTestAppSpringBoot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.arkhipov.MySecondTestAppSpringBoot.model.Request;
import ru.arkhipov.MySecondTestAppSpringBoot.model.Systems;

@Slf4j
@Service
@Qualifier("ModifySystemNameRequestService")
public class ModifySystemNameRequestService implements ModifyRequestService {
    @Override
    public void modify(Request request) {
        log.info("SystemName before modification: {}", request.getSystemName());
        request.setSystemName(Systems.CRM);
        log.info("SystemName after modification: {}", request.getSystemName());

        HttpEntity<Request> httpEntity = new HttpEntity<>(request);
        log.info("Отправка модифицированного request в Сервис 2: {}", request);
        new RestTemplate().exchange("http://localhost:8084/feedback", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {
        });
    }
}
