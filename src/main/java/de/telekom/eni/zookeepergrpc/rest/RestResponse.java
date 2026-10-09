package de.telekom.eni.zookeepergrpc.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestResponse(
        boolean success,
        String message
) {}
