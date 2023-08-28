package org.example.controller.graphcontroller;

import org.example.model.company.Organization;
import org.example.repository.companyrepository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class GraphOrganizationController {
    private final OrganizationRepository organizationRepository;

    @Autowired
    public GraphOrganizationController(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @GetMapping(value = "/organizations")
    public Flux<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @GetMapping(value = "/organization/{id}")
    public Mono<Organization> getOrganizationById(@PathVariable String id) {
        return organizationRepository.findByOrganizationId(Integer.valueOf(id));
    }
}
