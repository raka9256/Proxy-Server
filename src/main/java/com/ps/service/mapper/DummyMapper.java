package com.ps.service.mapper;

import com.ps.domain.*;
import com.ps.service.dto.DummyDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Dummy and its DTO DummyDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DummyMapper extends EntityMapper <DummyDTO, Dummy> {
    
    

}
