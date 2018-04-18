package com.ps.service.mapper;

import com.ps.domain.*;
import com.ps.service.dto.ProxyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Proxy and its DTO ProxyDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProxyMapper extends EntityMapper <ProxyDTO, Proxy> {
    
    

}
