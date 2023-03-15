package com.tomcoward.heterogeneousfaas.resourcemanager.integrations;

import software.amazon.awssdk.services.ecr.EcrClient;
import software.amazon.awssdk.services.ecr.model.*;

import java.util.Base64;

public class AWSECR {
    private final EcrClient client;

    public AWSECR() {
        client = EcrClient.builder()
                .region(AWSLambda.AWS_REGION)
                .build();
    }

    public String createRepository(String repositoryName) {
        try {
            CreateRepositoryRequest createRepositoryRequest = CreateRepositoryRequest.builder()
                    .repositoryName(repositoryName)
                    .build();

            return client.createRepository(createRepositoryRequest).repository().repositoryUri();
        } catch (RepositoryAlreadyExistsException ex) {
            // get existing repository and return URI
            DescribeRepositoriesRequest describeRepositoriesRequest = DescribeRepositoriesRequest.builder()
                    .repositoryNames(repositoryName)
                    .build();

            return client.describeRepositories(describeRepositoriesRequest).repositories().get(0).repositoryUri();
        }
    }

    public AWSECRCredentials getCredentials() {
        AuthorizationData authorizationData = getAuthorizationData();

        String authToken = authorizationData.authorizationToken();

        byte[] decodedToken = Base64.getDecoder().decode(authToken);
        String decodedString = new String(decodedToken);
        String[] tokenParts = decodedString.split(":");

        String username = tokenParts[0];
        String password = tokenParts[1];

        // get registry URL, removing "https://" from the start
        String registryUrl = authorizationData.proxyEndpoint().strip().replace("https://", "");

        return new AWSECRCredentials(username, password, registryUrl);
    }

    private AuthorizationData getAuthorizationData() {
        GetAuthorizationTokenRequest request = GetAuthorizationTokenRequest.builder().build();
        GetAuthorizationTokenResponse response = client.getAuthorizationToken(request);

        return response.authorizationData().get(0);
    }


    public class AWSECRCredentials {
        private final String username;
        private final String password;
        private final String registryUrl;

        public AWSECRCredentials(String username, String password, String registryUrl) {
            this.username = username;
            this.password = password;
            this.registryUrl = registryUrl;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getRegistryUrl() {
            return registryUrl;
        }
    }
}
