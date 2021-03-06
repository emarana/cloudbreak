package com.sequenceiq.cloudbreak.cloud.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Class that describes complete structure of infrastructure that needs to be started on the Cloud Provider
 */
public class CloudStack {

    private final List<Group> groups;

    private final Network network;

    private final Image image;

    private final String template;

    private final String loginUserName;

    private final String publicKey;

    private final Map<String, String> parameters;

    private final Map<String, String> tags;

    private final InstanceAuthentication instanceAuthentication;

    public CloudStack(Collection<Group> groups, Network network, Image image, Map<String, String> parameters, Map<String, String> tags, String template,
            InstanceAuthentication instanceAuthentication, String loginUserName, String publicKey) {
        this.groups = ImmutableList.copyOf(groups);
        this.network = network;
        this.image = image;
        this.parameters = ImmutableMap.copyOf(parameters);
        this.tags = ImmutableMap.copyOf(tags);
        this.template = template;
        this.instanceAuthentication = instanceAuthentication;
        this.loginUserName = loginUserName;
        this.publicKey = publicKey;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Network getNetwork() {
        return network;
    }

    public Image getImage() {
        return image;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public Security getCloudSecurity() {
        return groups.get(0).getSecurity();
    }

    public InstanceAuthentication getInstanceAuthentication() {
        return instanceAuthentication;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CloudStack{");
        sb.append("groups=").append(groups);
        sb.append(", network=").append(network);
        sb.append(", image=").append(image);
        sb.append('}');
        return sb.toString();
    }

    public String getTemplate() {
        return template;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getLoginUserName() {
        return loginUserName;
    }
}
