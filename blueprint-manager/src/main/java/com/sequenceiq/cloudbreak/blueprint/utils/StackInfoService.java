package com.sequenceiq.cloudbreak.blueprint.utils;

import java.io.IOException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.sequenceiq.cloudbreak.blueprint.BlueprintProcessingException;
import com.sequenceiq.cloudbreak.blueprint.templates.BlueprintStackInfo;
import com.sequenceiq.cloudbreak.util.JsonUtil;

@Component
public class StackInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StackInfoService.class);

    @Inject
    private BlueprintUtils blueprintUtils;

    public boolean hdfCluster(String blueprintText) {
        boolean hdfCluster;
        try {
            hdfCluster = "HDF".equals(blueprintStackInfo(blueprintText).getType().toUpperCase());
        } catch (BlueprintProcessingException e) {
            hdfCluster = false;
        }
        return hdfCluster;
    }

    public BlueprintStackInfo blueprintStackInfo(String blueprintText) throws BlueprintProcessingException {
        try {
            JsonNode root = JsonUtil.readTree(blueprintText);
            return new BlueprintStackInfo(blueprintUtils.getBlueprintHdpVersion(root), blueprintUtils.getBlueprintStackName(root));
        } catch (IOException e) {
            String message = String.format("Unable to detect BlueprintStackInfo from the source blueprint which was: %s.", blueprintText);
            LOGGER.warn(message);
            throw new BlueprintProcessingException(message, e);
        }
    }

}
