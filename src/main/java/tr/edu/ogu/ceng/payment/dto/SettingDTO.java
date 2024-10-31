package tr.edu.ogu.ceng.payment.dto;

import lombok.Data;

@Data
public class SettingDTO {
    private Long id;
    private String settingKey;
    private String settingValue;
}
