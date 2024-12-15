package dev.avorakh.shop.cdk;

import io.github.cdimascio.dotenv.Dotenv;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Size;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class CdkCartApiStack extends Stack {

    public static final String DB_HOST = "DB_HOST";
    public static final String DB_PORT = "DB_PORT";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    public static final String DB_NAME = "DB_NAME";

    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id) {
        this(scope, id, null);
    }

    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

        var dotenv = Dotenv.configure()
                .systemProperties()
                .directory("./asset")
                .load();

        var vpc = Vpc.fromLookup(this, "DB-VPC", VpcLookupOptions.builder()
                .vpcId(dotenv.get("DB_VPC_ID"))
                .build());

        var lambdaSecurityGroup = SecurityGroup.Builder.create(this, "Lambda-SG")
                .securityGroupName("LambdaSecurityGroup")
                .vpc(vpc)
                .allowAllOutbound(true)
                .build();

        var rdsSecurityGroup = SecurityGroup.fromSecurityGroupId(this, "DB-SG", dotenv.get("DB_SECURITY_GROUP_ID"));

        rdsSecurityGroup.addIngressRule(lambdaSecurityGroup, Port.POSTGRES, "Allow Lambda function to access RDS");

        var lambdaRole = createLambdaRole();

        var cartLambdaFunction = cartLambdaFunction(vpc, lambdaSecurityGroup, lambdaRole);

        cartLambdaFunction.addEnvironment(DB_HOST, dotenv.get(DB_HOST));
        cartLambdaFunction.addEnvironment(DB_PORT, dotenv.get(DB_PORT));
        cartLambdaFunction.addEnvironment(DB_USERNAME, dotenv.get(DB_USERNAME));
        cartLambdaFunction.addEnvironment(DB_PASSWORD, dotenv.get(DB_PASSWORD));
        cartLambdaFunction.addEnvironment(DB_NAME, dotenv.get(DB_NAME));

        var api = createApiGateway();

        var cartLambda = new LambdaIntegration(cartLambdaFunction, LambdaIntegrationOptions.builder()
                .proxy(true)
                .build());

        api.getRoot().addProxy(ProxyResourceOptions.builder()
                .anyMethod(true)
                .defaultIntegration(cartLambda)
                .build());

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

    private @NotNull Role createLambdaRole() {
        return Role.Builder.create(this, "LambdaExecutionRole")
                .assumedBy(new ServicePrincipal("lambda.amazonaws.com"))
                .managedPolicies(List.of(
                        ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaVPCAccessExecutionRole"),
                        ManagedPolicy.fromAwsManagedPolicyName("AmazonRDSFullAccess")
                ))
                .build();
    }

    private @NotNull Function cartLambdaFunction(IVpc vpc, ISecurityGroup sg, IRole role) {
        return Function.Builder.create(this, "cartServiceLambdaFunction")
                .functionName("cartService")
                .runtime(Runtime.NODEJS_20_X)
                .architecture(Architecture.ARM_64)
                .handler("main.handler")
                .code(Code.fromAsset("../dist"))
                .timeout(Duration.seconds(20))
                .memorySize(256)
                .allowPublicSubnet(true)
                .ephemeralStorageSize(Size.mebibytes(512))
                .vpc(vpc)
                .role(role)
                .securityGroups(List.of(sg))
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
