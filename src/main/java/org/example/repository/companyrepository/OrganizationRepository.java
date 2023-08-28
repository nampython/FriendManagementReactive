package org.example.repository.companyrepository;

import org.example.model.company.Organization;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface OrganizationRepository extends R2dbcRepository<Organization, Integer> {
    Mono<Organization> findByOrganizationId(Integer id);
}
