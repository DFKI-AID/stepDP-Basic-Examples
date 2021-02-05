# Basic Examples for StepDP Versions >= 0.9.0

This projects contains some code examples that show how to build a dialog system with StepDP. For each example, there is a more detailed description in the [StepDP documentation](http://stepdp.sb.dfki.de). The respective links are provided below. The following examples are currently available:

- **HelloWorldExample**: Most basic example, which shows how to define simple dialog rules (<http://stepdp.sb.dfki.de/stepdp/rules/simple_rule/>).
- **PatternConditionExample**: Shows how to define slightly more advanced conditions for dialog rules (<http://stepdp.sb.dfki.de/stepdp/rules/pattern_condition/>).
- **DeclarativeTypeBasedFusionExample**: Shows how to define fusion rules. (<http://stepdp.sb.dfki.de/stepdp/rules/declarative_type_based_fusion/>).

### How to run the examples

The application.yaml file specifies which example is executed. Per default, it selects the **HelloWorldExample**. To select a different example, e.g. the **PatternConditionExample**, replace

``` yaml
dialog:
  name: basic_examples.HelloWorldExample
```
with

``` yaml
dialog:
  name: basic_examples.PatternConditionExample
```

To run the selected example, simply run the *Main* class in your IDE.