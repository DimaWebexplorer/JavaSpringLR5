package ru.arkhipov.MySecondTestAppSpringBoot.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    /**
     * Уникальный идентификатор сообщения
     */
    @NotBlank(message = "uid должен присутствовать")
    @Size(max = 32, message = "uid должен быть не длиннее 32 символов")
    private String uid;

    /**
     * Уникальный идентификатор операции
     */
    @NotBlank(message = "operationUid должен присутствовать")
    @Size(max = 32, message = "operationUid должен быть не длиннее 32 символов")
    private String operationUid;

    /**
     * Имя системы отправителя
     */
    private Systems systemName;

    /**
     * Время создания сообщения
     */
    @NotBlank(message = "systemTime должен присутствовать")
    private String systemTime;

    /**
     * Наименование ресурса
     */
    private String source;

    /**
     * Должность сотрудника
     */
    private Positions position;

    /**
     * Заработная плата сотрудника
     */
    private Double salary;

    /**
     * Коэффициент премии
     */
    private Double bonus;

    /**
     * Количество рабочих дней в году
     */
    private Integer workDays;

    /**
     * Уникальный идентификатор коммуникации
     */
    @NotNull(message = "communicationId должен присутствовать")
    @Min(value = 1, message = "communicationId должен быть больше 0")
    @Max(value = 100000, message = "communicationId должен быть не больше 100000")
    private Integer communicationId;

    /**
     * Уникальный идентификатор шаблона
     */
    private Integer templateId;

    /**
     * Код продукта
     */
    private Integer productCode;

    /**
     * Смс код
     */
    private Integer smsCode;

    /**
     * Время получения запроса Сервисом 1
     */
    private Long receivedTime;

    @Override
    public String toString() {
        return "Request{" +
                "uid='" + uid + '\'' +
                ", operationUid='" + operationUid + '\'' +
                ", systemName='" + systemName + '\'' +
                ", systemTime='" + systemTime + '\'' +
                ", source='" + source + '\'' +
                ", position=" + position +
                ", salary=" + salary +
                ", bonus=" + bonus +
                ", workDays=" + workDays +
                ", communicationId=" + communicationId +
                ", templateId=" + templateId +
                ", productCode=" + productCode +
                ", smsCode=" + smsCode +
                ", receivedTime=" + receivedTime +
                '}';
    }
}
