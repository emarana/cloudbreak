package com.sequenceiq.cloudbreak.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public abstract class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credential_generator")
    @SequenceGenerator(name = "credential_generator", sequenceName = "credential_table")
    private Long id;

    private CloudPlatform cloudPlatform;

    private String name;

    public Credential() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CloudPlatform getCloudPlatform() {
        return cloudPlatform;
    }

    public void setCloudPlatform(CloudPlatform cloudPlatform) {
        this.cloudPlatform = cloudPlatform;
    }

    public abstract CloudPlatform cloudPlatform();

    public abstract User getOwner();

}
