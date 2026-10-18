package ru.arkhipov.MySecondTestAppSpringBoot.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.arkhipov.MySecondTestAppSpringBoot.exception.UnsupportedCodeException;
import ru.arkhipov.MySecondTestAppSpringBoot.exception.ValidationFailedException;
import ru.arkhipov.MySecondTestAppSpringBoot.model.*;
import ru.arkhipov.MySecondTestAppSpringBoot.service.AnnualBonusService;
import ru.arkhipov.MySecondTestAppSpringBoot.service.ModifyRequestService;
import ru.arkhipov.MySecondTestAppSpringBoot.service.ModifyResponseService;
import ru.arkhipov.MySecondTestAppSpringBoot.service.ValidationService;
import ru.arkhipov.MySecondTestAppSpringBoot.util.DateTimeUtil;

import java.util.Date;

@Slf4j
@RestController
public class MyController {
    private final ValidationService validationService;
    private final ModifyResponseService modifyResponseService;
    private final ModifyRequestService modifySourceRequestService;
    private final ModifyRequestService modifySystemNameRequestService;
    private final AnnualBonusService annualBonusService;

    @Autowired
    public MyController(
            ValidationService validationService,
            @Qualifier("ModifySystemTimeResponseService") ModifyResponseService modifyResponseService,
            @Qualifier("ModifySourceRequestService") ModifyRequestService modifySourceRequestService,
            @Qualifier("ModifySystemNameRequestService") ModifyRequestService modifySystemNameRequestService,
            AnnualBonusService annualBonusService
    ) {
        this.validationService = validationService;
        this.modifyResponseService = modifyResponseService;
        this.modifySourceRequestService = modifySourceRequestService;
        this.modifySystemNameRequestService = modifySystemNameRequestService;
        this.annualBonusService = annualBonusService;
    }

    @PostMapping(value = "/feedback")
    public ResponseEntity<Response> feedback(@Valid @RequestBody Request request, BindingResult bindingResult) {
        request.setReceivedTime(System.currentTimeMillis());
        log.info("request: {}", request);

        Response response = buildResponse(request);

        try {
            validationService.isValid(bindingResult);
            validationService.isUidValid(request.getUid());
            calculateBonuses(request, response);
        } catch (ValidationFailedException e) {
            return buildErrorResponse(response, ErrorCodes.VALIDATION_EXCEPTION, ErrorMessages.VALIDATION, HttpStatus.BAD_REQUEST, e);
        } catch (UnsupportedCodeException e) {
            return buildErrorResponse(response, ErrorCodes.UNSUPPORTED_CODE_EXCEPTION, ErrorMessages.UNSUPPORTED_CODE, HttpStatus.BAD_REQUEST, e);
        } catch (Exception e) {
            return buildErrorResponse(response, ErrorCodes.UNKNOWN_EXCEPTION, ErrorMessages.UNKNOWN, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        modifyResponseService.modify(response);
        modifySourceRequestService.modify(request);
        modifySystemNameRequestService.modify(request);

        log.info("response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Response buildResponse(Request request) {
        return Response.builder()
                .uid(request.getUid())
                .operationUid(request.getOperationUid())
                .systemTime(DateTimeUtil.getCustomFormat().format(new Date()))
                .code(Codes.SUCCESS)
                .errorCode(ErrorCodes.EMPTY)
                .errorMessage(ErrorMessages.EMPTY)
                .build();
    }

    private void calculateBonuses(Request request, Response response) {
        response.setAnnualBonus(annualBonusService.calculate(
                request.getPosition(), request.getSalary(), request.getBonus(), request.getWorkDays()));
        response.setQuarterlyBonus(annualBonusService.calculateQuarterly(
                request.getPosition(), request.getSalary(), request.getBonus(), request.getWorkDays()));
    }

    private ResponseEntity<Response> buildErrorResponse(Response response, ErrorCodes errorCode,
                                                        ErrorMessages errorMessage, HttpStatus status, Exception e) {
        response.setCode(Codes.FAILED);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        log.error("{}: {}", errorCode, e.getMessage());
        return new ResponseEntity<>(response, status);
    }
}
