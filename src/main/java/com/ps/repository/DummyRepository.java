package com.ps.repository;

import com.ps.domain.Dummy;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Dummy entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DummyRepository extends MongoRepository<Dummy, String> {

}
