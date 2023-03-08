package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import com.tomcoward.heterogeneousfaas.resourcemanager.exceptions.IntegrationException;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AWSIAM {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final static String AWS_IAM_ROLE_NAME = "heterogeneous-faas-lambda-role";

    private final IamClient iamClient;

    public AWSIAM() {
        this.iamClient = IamClient.builder()
                .region(AWSLambda.AWS_REGION)
                .build();
    }


    public String createIamRole() throws IntegrationException {
        try {
            CreateRoleRequest createRoleRequest = CreateRoleRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .assumeRolePolicyDocument(
                            "{\n" +
                                    "    \"Version\": \"2012-10-17\",\n" +
                                    "    \"Statement\": [\n" +
                                    "        {\n" +
                                    "            \"Effect\": \"Allow\",\n" +
                                    "            \"Action\": [\n" +
                                    "                \"sts:AssumeRole\"\n" +
                                    "            ],\n" +
                                    "            \"Principal\": {\n" +
                                    "                \"Service\": [\n" +
                                    "                    \"lambda.amazonaws.com\"\n" +
                                    "                ]\n" +
                                    "            }\n" +
                                    "        }\n" +
                                    "    ]\n" +
                                    "}"
                    )
                    .build();

            CreateRoleResponse createRoleResponse = iamClient.createRole(createRoleRequest);

            // Attach "AWSLambda_FullAccess" policy to role to allow it perms
            AttachRolePolicyRequest attachFullAccessRolePolicyRequest = AttachRolePolicyRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .policyArn("arn:aws:iam::aws:policy/AWSLambda_FullAccess")
                    .build();

            iamClient.attachRolePolicy(attachFullAccessRolePolicyRequest);

            // Attach "AWSLambdaBasicExecutionRole" policy to role to allow it perms
            AttachRolePolicyRequest attachExecutionRolePolicyRequest = AttachRolePolicyRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .policyArn("arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole")
                    .build();

            iamClient.attachRolePolicy(attachExecutionRolePolicyRequest);

            return createRoleResponse.role().arn();
        } catch (EntityAlreadyExistsException ex) {
            // role already exists, so get its arn
            GetRoleRequest getRoleRequest = GetRoleRequest.builder()
                    .roleName(AWS_IAM_ROLE_NAME)
                    .build();

            GetRoleResponse getRoleResponse = iamClient.getRole(getRoleRequest);

            return getRoleResponse.role().arn();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error creating AWS Lambda IAM role", ex);
            throw new IntegrationException("There was an issue setting up the AWS Lambda environment (IAM)");
        }
    }
}
