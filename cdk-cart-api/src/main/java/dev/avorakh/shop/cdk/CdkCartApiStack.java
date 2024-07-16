package dev.avorakh.shop.cdk;

import org.jetbrains.annotations.Nullable;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;


public class CdkCartApiStack extends Stack {
    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id) {
        this(scope, id, null);
    }

    public CdkCartApiStack(@Nullable Construct scope, @Nullable String id, @Nullable StackProps props) {
        super(scope, id, props);

    }
}
