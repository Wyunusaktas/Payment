package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class SettingDTO extends BaseDTO {
    private Long id;
    private String settingKey;
    private String settingValue;
}
