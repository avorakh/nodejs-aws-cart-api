package dev.avorakh.shop.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class CdkCartApiApp {
    public static void main(final String[] args) {
        var app = new App();

        var pr = StackProps.builder().build();

        new CdkCartApiStack(app, "CdkCartApiStack", pr);

        app.synth();
    }
}

