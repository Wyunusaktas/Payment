package tr.edu.ogu.ceng.payment.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tr.edu.ogu.ceng.payment.dto.BaseDTO;
import tr.edu.ogu.ceng.payment.entity.BaseEntity;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // BaseDTO alanlarını miras alınmış DTO'larda eşleştir
        modelMapper.typeMap(BaseEntity.class, BaseDTO.class)
                .addMapping(BaseEntity::getCreatedBy, BaseDTO::setCreatedBy)
                .addMapping(BaseEntity::getCreatedAt, BaseDTO::setCreatedAt)
                .addMapping(BaseEntity::getUpdatedBy, BaseDTO::setUpdatedBy)
                .addMapping(BaseEntity::getUpdatedAt, BaseDTO::setUpdatedAt)
                .addMapping(BaseEntity::getDeletedBy, BaseDTO::setDeletedBy)
                .addMapping(BaseEntity::getDeletedAt, BaseDTO::setDeletedAt)
                .addMapping(BaseEntity::getVersion, BaseDTO::setVersion);

        return modelMapper;
    }
}
