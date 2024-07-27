package dev.avorakh.shop.cdk;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class CdkCartApiApp {
    public static void main(final String[] args) {
        var app = new App();

        var dotenv = Dotenv.configure()
                .systemProperties()
                .directory("./asset")
                .load();

        var pr = StackProps.builder()
                .env(Environment.builder()
                        .region(dotenv.get("CDK_DEFAULT_REGION"))
                        .account(dotenv.get("CDK_DEFAULT_ACCOUNT"))
                        .build())
                .build();

        new CdkCartApiStack(app, "CdkCartApiStack", pr);

        app.synth();
    }
}

