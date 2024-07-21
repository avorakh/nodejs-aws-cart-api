package dev.avorakh.shop.cdk;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Size;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class CdkCartApiStack extends Stack {
    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id) {
        this(scope, id, null);
    }

    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

        var cartLambdaFunction = cartLambdaFunction();

        var api = createApiGateway();

        var cartLambda = new LambdaIntegration(cartLambdaFunction, LambdaIntegrationOptions.builder()
                .proxy(true)
                .build());

        api.getRoot().addProxy(ProxyResourceOptions.builder()
                .anyMethod(true)
                .defaultIntegration(cartLambda)
                .build());

        api.getRoot().addMethod("GET", cartLambda);

        api.addGatewayResponse("GatewayResponse4XX", GatewayResponseOptions.builder()
                .type(ResponseType.DEFAULT_4_XX)
                .responseHeaders(Map.of(
                        "Access-Control-Allow-Origin", "'*'",
                        "Access-Control-Allow-Headers", "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
                        "Access-Control-Allow-Methods", "'*'"
                ))
                .build());

        doDeployment(api);
    }

    private @NotNull Function cartLambdaFunction() {
        return Function.Builder.create(this, "cartServiceLambdaFunction")
                .functionName("cartService")
                .runtime(Runtime.NODEJS_20_X)
                .architecture(Architecture.ARM_64)
                .handler("main.handler")
                .code(Code.fromAsset("../dist"))
                .timeout(Duration.seconds(20))
                .memorySize(256)
                .ephemeralStorageSize(Size.mebibytes(512))
                .build();
    }


    private @NotNull RestApi createApiGateway() {

        var corsOptions = CorsOptions.builder()
                .allowOrigins(Cors.ALL_ORIGINS)
                .allowHeaders(Cors.DEFAULT_HEADERS)
                .allowMethods(Cors.ALL_METHODS)
                .statusCode(200)
                .build();

        return new RestApi(
                this,
                "cartApi",
                RestApiProps.builder()
                        .restApiName("Cart Service")
                        .deploy(true)
                        .deployOptions(StageOptions.builder().stageName("dev").build())
                        .endpointTypes(List.of(EndpointType.REGIONAL))
                        .defaultCorsPreflightOptions(corsOptions)
                        .build());
    }

    private void doDeployment(RestApi api) {
        Deployment.Builder.create(this, "CartServiceDevDeployment")
                .api(api)
                .build();
    }
}
